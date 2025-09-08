package com.example.warehouse;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.warehouse.S3Images.S3Service;
import com.example.warehouse.camunda.ComplianceCheckResponse;
import com.example.warehouse.camunda.ComplianceCheckWorker;
import com.example.warehouse.camunda.OrderConfirmRequest;
import com.example.warehouse.camunda.OrderOrchestrationService;
import com.example.warehouse.currency.CurrencyFilter;
import com.example.warehouse.currency.CurrencyService;
import com.example.warehouse.currency.CurrencyServiceClient;
import com.example.warehouse.orders.CustomerRepository;
import com.example.warehouse.orders.OrderEntity;
import com.example.warehouse.orders.OrderItemRepository;
import com.example.warehouse.orders.OrderRepository;
import com.example.warehouse.orders.OrderService;
import com.example.warehouse.orders.OrderStatus;
import com.example.warehouse.persistence.repository.ProductRepository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.response.Topology;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@DirtiesContext
//@Import(TestConfig.class)
public class OrderConfirmationProcessTest {

    @Container
    public static GenericContainer<?> zeebeContainer = new GenericContainer<>(
            DockerImageName.parse("camunda/zeebe:8.1.13"))
            .withExposedPorts(26500)
            .withEnv("ZEEBE_BROKER_GATEWAY_NETWORK_HOST", "0.0.0.0")
            .withEnv("ZEEBE_BROKER_GATEWAY_ENABLE", "true")
            .withEnv("ZEEBE_BROKER_CLUSTER_PARTITIONSCOUNT", "1")
            .withEnv("ZEEBE_BROKER_CLUSTER_REPLICATIONFACTOR", "1")
            .withEnv("ZEEBE_BROKER_CLUSTER_CLUSTERSIZE", "1")
            .withEnv("ZEEBE_BROKER_DATA_SNAPSHOTPERIOD", "1m")
            .withEnv("ZEEBE_BROKER_NETWORK_HOST", "0.0.0.0")
            .withEnv("ZEEBE_GATEWAY_NETWORK_HOST", "0.0.0.0")
            .withEnv("ZEEBE_BROKER_CLUSTER_NODEID", "0")
            .withStartupTimeout(Duration.ofMinutes(5))
            .waitingFor(Wait.forLogMessage(".*Broker is ready!.*", 1)
                    .withStartupTimeout(Duration.ofMinutes(5)));

    private static ZeebeClient zeebeClient;

    @DynamicPropertySource
    static void zeebeProperties(DynamicPropertyRegistry registry) {
        // Ensure container is started first
        if (!zeebeContainer.isRunning()) {
            throw new IllegalStateException("Zeebe container is not running");
        }

        String gatewayAddress = String.format("%s:%d",
                zeebeContainer.getHost(),
                zeebeContainer.getMappedPort(26500));

        registry.add("zeebe.client.broker.gatewayAddress", () -> gatewayAddress);
        registry.add("zeebe.client.worker.defaultName", () -> "test-worker");
        registry.add("zeebe.client.worker.defaultType", () -> "test");
        registry.add("zeebe.client.requestTimeout", () -> "30s");
    }

    @BeforeAll
    static void setup() {
        // Print container logs for debugging
        zeebeContainer.followOutput(outputFrame -> {
            System.out.println("ZEBBE LOG: " + outputFrame.getUtf8String());
        });

        // Initialize Zeebe client after container is ready
        String gatewayAddress = String.format("%s:%d",
                zeebeContainer.getHost(),
                zeebeContainer.getMappedPort(26500));

        zeebeClient = ZeebeClient.newClientBuilder()
                .gatewayAddress(gatewayAddress)
                .usePlaintext()
                .build();

        deployProcess();
    }

    static void deployProcess() {
        // Wait for Zeebe to be ready with better error handling
        Awaitility.await()
                .atMost(120, TimeUnit.SECONDS)
                .pollInterval(5, TimeUnit.SECONDS)
                .ignoreExceptions()
                .until(() -> {
                    try {
                        Topology topology = zeebeClient.newTopologyRequest().send().join();
                        System.out.println("Zeebe topology: " + topology);
                        return true;
                    } catch (Exception e) {
                        System.err.println("Failed to connect to Zeebe: " + e.getMessage());
                        throw e;
                    }
                });

        // Deploy the process
        try {
            DeploymentEvent deployment = zeebeClient.newDeployResourceCommand()
                    .addResourceFromClasspath("camunda/order-confirmation.bpmn")
                    .send()
                    .join();
            System.out.println("Process deployed: " + deployment.getProcesses());
        } catch (Exception e) {
            System.err.println("Failed to deploy process: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void tearDown() {
        if (zeebeClient != null) {
            zeebeClient.close();
        }
    }


    @MockBean
    private CurrencyServiceClient currencyServiceClient;

    @MockBean
    private CurrencyService currencyService;

    @MockBean
    private CurrencyFilter currencyFilter;

    @MockBean
    private S3Client s3Client;

    @MockBean
    private S3Service s3Service;

    @MockBean
    private ComplianceCheckWorker complianceCheckWorker;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockBean
    private KafkaTemplate<String, byte[]> kafkaTemplateByteArray;

    @MockBean
    private KafkaTemplate<String, Object> kafkaObjectTemplate;

    @MockBean
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @MockBean
    private WebClient.Builder webClientBuilder;

    @MockBean
    private WebClient webClient;

    @MockBean
    private OrderOrchestrationService orderOrchestrationService;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderOrchestrationService orchestrationService;


    @Test
    public void testHappyPathOrderConfirmation() throws Exception {
        // 1. Создаем тестовые данные
        UUID orderId = UUID.randomUUID();
        OrderEntity order = OrderEntity.builder()
                .id(orderId)
                .customerId(1L)
                .status(OrderStatus.CREATED)
                .deliveryAddress("Test Address")
                .inn("1234567890")
                .accountNumber("ACC123")
                .totalAmount(new BigDecimal("10.50"))
                .build();
        orderRepository.save(order);

        OrderConfirmRequest confirmRequest = new OrderConfirmRequest();
        confirmRequest.setDeliveryAddress("Test Address");
        confirmRequest.setInn("1234567890");
        confirmRequest.setAccountNumber("ACC123");
        confirmRequest.setTotalAmount(new BigDecimal("10.50"));
        confirmRequest.setLogin("testuser");

        // 2. Запускаем процесс
        UUID businessKey = orchestrationService.startOrderConfirmationProcess(orderId, confirmRequest);

        // 3. Ждем, пока процесс дойдет до комплаенс проверки
        Awaitility.await()
                .atMost(30, TimeUnit.SECONDS)
                .until(() -> {
                    List<ActivatedJob> jobs = zeebeClient.newActivateJobsCommand()
                            .jobType("compliance-check")
                            .maxJobsToActivate(10)
                            .send()
                            .join()
                            .getJobs();
                    return !jobs.isEmpty();
                });

        // 4. Симулируем успешный ответ от комплаенс
        ComplianceCheckResponse complianceResponse = new ComplianceCheckResponse();
        complianceResponse.setApproved(true);
        complianceResponse.setBusinessKey(businessKey.toString());

        // 5. Симулируем успешную регистрацию договора
        stubFor(post(urlEqualTo("/api/contracts/register"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"contractId\": \"CONTRACT-123\"}")));

        // 6. Симулируем успешную регистрацию доставки
        stubFor(post(urlEqualTo("/api/deliveries/register"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"deliveryDate\": \"2024-01-15T10:00:00Z\"}")));

        // 7. Симулируем успешную оплату
        stubFor(post(urlEqualTo("/api/payments/process"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"success\": true}")));

        // 8. Ждем завершения процесса
        Awaitility.await()
                .atMost(60, TimeUnit.SECONDS)
                .until(() -> {
                    OrderEntity updatedOrder = orderRepository.findById(orderId).orElseThrow();
                    return OrderStatus.DONE.equals(updatedOrder.getStatus());
                });

        // 9. Проверяем результаты
        OrderEntity resultOrder = orderRepository.findById(orderId).orElseThrow();
        assertThat(resultOrder.getStatus()).isEqualTo(OrderStatus.DONE);
        assertThat(resultOrder.getContractId()).isEqualTo("CONTRACT-123");
        assertThat(resultOrder.getDeliveryDate()).isNotNull();
        assertThat(resultOrder.getBusinessKey()).isEqualTo(businessKey);
    }
}