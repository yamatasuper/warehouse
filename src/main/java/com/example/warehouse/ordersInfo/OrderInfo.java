package com.example.warehouse.ordersInfo;

import com.example.warehouse.orders.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfo {
    private UUID id;
    private CustomerInfo customer;
    private OrderStatus status;
    private String deliveryAddress;
    private Integer quantity;
}
