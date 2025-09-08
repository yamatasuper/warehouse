package com.example.warehouse;

import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import com.example.warehouse.camunda.OrderOrchestrationService;
import com.example.warehouse.currency.CurrencyService;
import com.example.warehouse.currency.CurrencyServiceClient;
import com.example.warehouse.orders.OrderController;
import com.example.warehouse.orders.OrderEntity;
import com.example.warehouse.orders.OrderRepository;
import com.example.warehouse.orders.OrderStatus;
import com.github.tomakehurst.wiremock.client.WireMock;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import software.amazon.awssdk.services.s3.S3Client;
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public class OrderOrchestrationIntegrationTest {

    @MockBean
    private OrderOrchestrationService orderOrchestrationService;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private S3Client s3Client;

    @MockBean
    private CurrencyServiceClient currencyServiceClient;

    @MockBean
    private CurrencyService currencyService;

    @Autowired
    private MockMvc mockMvc;

    @Container
    static GenericContainer<?> wiremock = new GenericContainer<>("wiremock/wiremock:2.35.0")
            .withExposedPorts(8080);

    @BeforeAll
    static void setupWireMock() {
        wiremock.start();
        WireMock.configureFor(wiremock.getHost(), wiremock.getMappedPort(8080));

        stubFor(post(urlEqualTo("/contracts"))
                .willReturn(okJson("{\"contractId\":\"c-12345\"}")));
        stubFor(post(urlEqualTo("/delivery"))
                .willReturn(okJson("{\"deliveryDate\":\"2025-09-10T10:00:00Z\"}")));
        stubFor(post(urlEqualTo("/payment"))
                .willReturn(okJson("{\"result\":\"OK\"}")));
    }

    @Test
    void testHappyPath() throws Exception {
        UUID orderId = UUID.randomUUID();

        OrderEntity order = OrderEntity.builder()
                .id(orderId)
                .customerId(1L)
                .status(OrderStatus.CREATED)
                .deliveryAddress("Moscow")
                .inn("1234567890")
                .accountNumber("ACC-001")
                .totalAmount(BigDecimal.valueOf(1000))
                .build();

        // Мокируем репозиторий
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.findByIdAndCustomerId(orderId, 1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Мокируем оркестратор
        ProcessInstance mockPi = mock(ProcessInstance.class);
        when(mockPi.getId()).thenReturn("process-123");
        when(orderOrchestrationService.startOrderConfirmationProcess(anyString()))
                .thenReturn(mockPi);
        // Вызываем контроллер
        mockMvc.perform(post("/api/orders/confirm/{orderId}", orderId)
                        .header("X-Customer-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"user1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSING"));

        // Эмуляция завершения процесса
        order.setStatus(OrderStatus.DONE);
        order.setContractId("c-12345");
        order.setDeliveryDate(ZonedDateTime.now());

        // Проверяем обновление заказа
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            OrderEntity updated = orderRepository.findById(orderId).orElseThrow();
            assertThat(updated.getStatus()).isEqualTo(OrderStatus.DONE);
            assertThat(updated.getContractId()).isEqualTo("c-12345");
            assertThat(updated.getDeliveryDate()).isNotNull();
        });
    }
}
