package com.example.warehouse.orders;

import java.math.BigDecimal;
import java.util.UUID;

public interface OrderItemProjection {
    UUID getId();
    UUID getOrderId();
    UUID getProductId();
    BigDecimal getQuantity();
    BigDecimal getPrice();
    String getProductName();
}