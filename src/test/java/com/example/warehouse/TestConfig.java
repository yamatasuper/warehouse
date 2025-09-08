package com.example.warehouse;

import com.example.warehouse.camunda.ComplianceCheckWorker;
import com.example.warehouse.camunda.OrderOrchestrationService;
import com.example.warehouse.currency.CurrencyFilter;
import com.example.warehouse.currency.CurrencyService;
import com.example.warehouse.currency.CurrencyServiceClient;
import com.example.warehouse.orders.CustomerRepository;
import com.example.warehouse.orders.OrderItemRepository;
import com.example.warehouse.orders.OrderRepository;
import com.example.warehouse.persistence.repository.ProductRepository;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import io.camunda.zeebe.client.ZeebeClient;

@TestConfiguration
public class TestConfig {


    @MockBean
    private CurrencyServiceClient currencyServiceClient;

    @MockBean
    private CurrencyService currencyService;

    @MockBean
    private CurrencyFilter currencyFilter;

    @MockBean
    private ZeebeClient zeebeClient;

    @MockBean
    private ComplianceCheckWorker complianceCheckWorker;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockBean
    private KafkaTemplate<String, byte[]> kafkaTemplateByteArray;

    @MockBean
    private KafkaTemplate<String, Object> kafkaObjectTemplate;

    @MockBean
    private org.springframework.kafka.config.KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

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
}