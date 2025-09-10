package com.example.warehouse.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private String event;
    private UUID orderId;
    private Long customerId;
    private String deliveryAddress;
    private String status;
    private List<OrderItemEvent> products;

    public enum EventType {
        CREATE_ORDER, UPDATE_ORDER, DELETE_ORDER, UPDATE_ORDER_STATUS
    }
}

