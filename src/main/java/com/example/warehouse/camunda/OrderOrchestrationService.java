package com.example.warehouse.camunda;

import com.example.warehouse.camunda.OrderConfirmRequest;
import com.example.warehouse.orders.OrderEntity;
import com.example.warehouse.orders.OrderRepository;
import com.example.warehouse.orders.OrderStatus;
import com.example.warehouse.orders.ResourceNotFoundException;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.camunda.zeebe.client.ZeebeClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderOrchestrationService {

    private ZeebeClient zeebeClient;
    private OrderRepository orderRepository;

    public UUID startOrderConfirmationProcess(UUID orderId, OrderConfirmRequest request) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        UUID businessKey = UUID.randomUUID();

        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", orderId.toString());
        variables.put("customerId", order.getCustomerId());
        variables.put("deliveryAddress", request.getDeliveryAddress());
        variables.put("inn", request.getInn());
        variables.put("accountNumber", request.getAccountNumber());
        variables.put("totalAmount", request.getTotalAmount());
        variables.put("login", request.getLogin());
        variables.put("businessKey", businessKey.toString());

        zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId("order-confirmation-process")
                .latestVersion()
                .variables(variables)
                .send()
                .join();

        // Сохраняем businessKey в заказ
        order.setBusinessKey(businessKey);
        order.setStatus(OrderStatus.PROCESSING);
        orderRepository.save(order);

        log.info("Started order confirmation process for order: {} with businessKey: {}", orderId, businessKey);
        return businessKey;
    }
}