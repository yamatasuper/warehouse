package com.example.warehouse;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.warehouse.camunda.ComplianceCheckWorker;
import com.example.warehouse.camunda.OrderOrchestrationService;
import com.example.warehouse.camunda.dto.ComplianceResponseMessage;
import com.example.warehouse.camunda.dto.OrderConfirmRequest;
import com.example.warehouse.currency.CurrencyFilter;
import com.example.warehouse.currency.CurrencyService;
import com.example.warehouse.currency.CurrencyServiceClient;
import com.example.warehouse.currency.WebClientConfig;
import com.example.warehouse.orders.OrderEntity;
import com.example.warehouse.orders.OrderRepository;
import com.example.warehouse.orders.OrderStatus;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.camunda.zeebe.client.ZeebeClient;
import software.amazon.awssdk.services.s3.S3Client;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.Deployment;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
public class OrderOrchestrationIntegrationTest {

    private static WireMockServer wireMockServer;

    @MockBean
    private CurrencyServiceClient currencyServiceClient;

    @MockBean
    private CurrencyService currencyService;

    @MockBean
    private CurrencyFilter currencyFilter;

    @MockBean
    private S3Client s3Client;

    @MockBean
    private com.example.warehouse.S3Images.S3Service s3Service;

    @MockBean
    private ZeebeClient zeebeClient;


    private ComplianceCheckWorker complianceCheckWorker;

    @MockBean
    private KafkaTemplate<String, String> kafkaStringTemplate;

    @MockBean
    private KafkaTemplate<String, byte[]> kafkaByteArrayTemplate;

    @MockBean
    private KafkaTemplate<String, Object> kafkaObjectTemplate;

    @MockBean
    private org.springframework.kafka.config.KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @MockBean
    private WebClientConfig webClientConfig;

    @Autowired
    private OrderOrchestrationService orchestrationService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProcessEngine processEngine;

    private RepositoryService repositoryService;

    @BeforeAll
    public static void setup() {
        // Start WireMock server on a random port
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();

        // Configure WireMock for the test
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @BeforeEach
    public void setupServices() {
        // Get RepositoryService from ProcessEngine
        repositoryService = processEngine.getRepositoryService();
        deployProcess();
    }

    public void deployProcess() {
        // Deploy your BPMN process before each test
        try {
            Deployment deployment = repositoryService.createDeployment()
                    .addClasspathResource("processes/order-confirmation.bpmn")
                    .name("Order Confirmation Process Deployment")
                    .deploy();

            System.out.println("Process deployed: " + deployment.getId());

            // Verify deployment was successful
            long processDefinitionCount = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey("Process_0twprp2")
                    .count();

            if (processDefinitionCount == 0) {
                throw new IllegalStateException("Process definition was not deployed successfully");
            }
        } catch (Exception e) {
            System.err.println("Failed to deploy process: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    public static void tearDown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    public void testHappyPath() throws Exception {
        // 1. Setup test data
        OrderEntity order = createTestOrder();

        // 2. Mock external services
        stubFor(post(urlEqualTo("/api/contracts/register"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"contractId\": \"CONTRACT_123\"}")));

        stubFor(post(urlEqualTo("/api/deliveries/register"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"deliveryDate\": \"2024-01-15T10:00:00Z\"}")));

        stubFor(post(urlEqualTo("/api/payments/process"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"success\": true}")));

        // 3. Start orchestration
        OrderConfirmRequest request = new OrderConfirmRequest();
        // Set request properties if needed

        // 3. Start orchestration
        UUID businessKey = orchestrationService.startOrderConfirmationProcess(order.getId(), request);

// Получаем ID процесса по businessKey
        String processInstanceId = processEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceBusinessKey(businessKey.toString())
                .singleResult()
                .getId();

// Устанавливаем переменную для этого конкретного процесса
        processEngine.getRuntimeService()
                .setVariable(processInstanceId, "complianceApproved", true);


        // 4. Simulate compliance approval
        ComplianceResponseMessage complianceResponse = new ComplianceResponseMessage();
        complianceResponse.setBusinessKey(businessKey.toString());
        complianceResponse.setApproved(true);

        // Use the mocked kafka template
        kafkaObjectTemplate.send("compliance-check-responses", businessKey.toString(), complianceResponse);

        // 5. Wait for process completion
        await().atMost(30, TimeUnit.SECONDS)
                .until(() -> orderRepository.findById(order.getId())
                        .map(OrderEntity::getStatus)
                        .orElse(null) == OrderStatus.CONFIRMED);

        // 6. Verify results
        OrderEntity updatedOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertEquals(OrderStatus.CONFIRMED, updatedOrder.getStatus());
        assertEquals("CONTRACT_123", updatedOrder.getContractId());
        assertNotNull(updatedOrder.getDeliveryDate());

        // Verify external calls
        verify(postRequestedFor(urlEqualTo("/api/contracts/register")));
        verify(postRequestedFor(urlEqualTo("/api/deliveries/register")));
        verify(postRequestedFor(urlEqualTo("/api/payments/process")));
    }

    private OrderEntity createTestOrder() {
        OrderEntity order = new OrderEntity();
        order.setId(UUID.randomUUID());
        order.setCustomerId(1L);
        order.setStatus(OrderStatus.CREATED);
        order.setDeliveryAddress("Test Address");
        order.setTotalAmount(new BigDecimal("999.99"));
        return orderRepository.save(order);
    }
}