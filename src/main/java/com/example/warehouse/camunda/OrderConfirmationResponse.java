package com.example.warehouse.camunda;

import com.example.warehouse.orders.OrderStatus;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderConfirmationResponse {
    private UUID orderId;
    private UUID businessKey;
    private OrderStatus status;
}
