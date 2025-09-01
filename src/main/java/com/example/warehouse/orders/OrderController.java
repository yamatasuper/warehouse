package com.example.warehouse.orders;

import com.example.warehouse.camunda.OrderOrchestrationService;
import com.example.warehouse.camunda.dto.OrderConfirmRequest;
import com.example.warehouse.camunda.dto.OrderConfirmationResponse;
import com.example.warehouse.controller.IdResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderOrchestrationService orchestrationService;

    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<OrderConfirmationResponse> confirmOrder(
            @RequestHeader("X-Customer-Id") Long customerId,
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderConfirmRequest request) {

        UUID businessKey = orchestrationService.startOrderConfirmationProcess(
                orderId, request
        );

        return ResponseEntity.ok(new OrderConfirmationResponse(
                orderId,
                businessKey,
                OrderStatus.PROCESSING
        ));
    }

    @PostMapping
    public ResponseEntity<IdResponse> createOrder(
            @RequestHeader("X-Customer-Id") Long customerId,
            @Valid @RequestBody OrderCreateRequest request) {
        UUID orderId = orderService.createOrder(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(orderId));
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrder(
            @RequestHeader("X-Customer-Id") Long customerId,
            @PathVariable UUID orderId) {
        return orderService.getOrder(orderId, customerId);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(
            @RequestHeader("X-Customer-Id") Long customerId,
            @PathVariable UUID orderId) {
        orderService.cancelOrder(orderId, customerId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok().build();
    }
}
