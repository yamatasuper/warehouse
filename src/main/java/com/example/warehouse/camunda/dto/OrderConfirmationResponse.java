package com.example.warehouse.camunda.dto;

import com.example.warehouse.orders.OrderStatus;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderConfirmationResponse {
    private UUID orderId;
    private UUID businessKey;
    private OrderStatus status;
    private String message;

    public OrderConfirmationResponse(UUID orderId, UUID businessKey, OrderStatus orderStatus) {
    }
}
