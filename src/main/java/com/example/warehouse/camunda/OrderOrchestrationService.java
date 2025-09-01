package com.example.warehouse.camunda;

import com.example.warehouse.camunda.dto.ComplianceCheckMessage;
import com.example.warehouse.camunda.dto.OrderConfirmRequest;
import com.example.warehouse.camunda.dto.PaymentRequest;
import com.example.warehouse.camunda.dto.PaymentResponse;
import com.example.warehouse.camunda.dto.RegisterContractRequest;
import com.example.warehouse.camunda.dto.RegisterContractResponse;
import com.example.warehouse.camunda.dto.RegisterDeliveryRequest;
import com.example.warehouse.camunda.dto.RegisterDeliveryResponse;
import com.example.warehouse.orders.BusinessException;
import com.example.warehouse.orders.OrderEntity;
import com.example.warehouse.orders.OrderRepository;
import com.example.warehouse.orders.ResourceNotFoundException;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.example.warehouse.orders.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderOrchestrationService {

    private final RuntimeService runtimeService;
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // 1. Запуск процесса оркестрации
    public UUID startOrderConfirmationProcess(UUID orderId, @Valid OrderConfirmRequest request) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        UUID businessKey = UUID.randomUUID();

        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", orderId.toString());
        variables.put("customerId", order.getCustomerId());
        variables.put("deliveryAddress", order.getDeliveryAddress());
        variables.put("inn", request.getInn());
        variables.put("accountNumber", request.getAccountNumber());
        variables.put("totalAmount", order.getTotalAmount());
        variables.put("login", getCustomerLogin(order.getCustomerId()));
        variables.put("businessKey", businessKey.toString());

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                "Process_0twprp2",
                businessKey.toString(),
                variables
        );

        order.setBusinessKey(businessKey);
        order.setStatus(OrderStatus.PROCESSING);
        order.setInn(request.getInn());
        order.setAccountNumber(request.getAccountNumber());
        orderRepository.save(order);

        return businessKey;
    }

    // 2. Compliance Check - отправка в Kafka
    public void sendComplianceCheck(String businessKey, String login, String inn) {
        ComplianceCheckMessage message = new ComplianceCheckMessage();
        message.setBusinessKey(businessKey);
        message.setLogin(login);
        message.setInn(inn);
        message.setTimestamp(Instant.now());

        kafkaTemplate.send("compliance-check-requests", businessKey, message);
    }

    // 3. Регистрация договора - REST интеграция
    public String registerContract(String inn, String accountNumber) {
        RegisterContractRequest request = new RegisterContractRequest(inn, accountNumber);

        ResponseEntity<RegisterContractResponse> response = restTemplate.postForEntity(
                "http://contract-service/api/contracts/register",
                request,
                RegisterContractResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody().getContractId();
        }
        throw new BusinessException("Failed to register contract");
    }

    // 4. Регистрация доставки
    public ZonedDateTime registerDelivery(String deliveryAddress, UUID orderId) {
        RegisterDeliveryRequest request = new RegisterDeliveryRequest(
                deliveryAddress,
                orderId.toString()
        );

        ResponseEntity<RegisterDeliveryResponse> response = restTemplate.postForEntity(
                "http://delivery-service/api/deliveries/register",
                request,
                RegisterDeliveryResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody().getDeliveryDate();
        }
        throw new BusinessException("Failed to register delivery");
    }

    // 5. Оплата
    public boolean processPayment(UUID orderId, BigDecimal amount, String accountNumber) {
        PaymentRequest request = new PaymentRequest(
                orderId.toString(),
                amount,
                accountNumber
        );

        ResponseEntity<PaymentResponse> response = restTemplate.postForEntity(
                "http://payment-service/api/payments/process",
                request,
                PaymentResponse.class
        );

        return response.getStatusCode().is2xxSuccessful() &&
                response.getBody() != null &&
                response.getBody().isSuccess();
    }

    private String getCustomerLogin(Long customerId) {
        // Реализация получения логина пользователя
        return "user_" + customerId;
    }
}
