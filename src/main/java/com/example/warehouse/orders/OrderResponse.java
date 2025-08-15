package com.example.warehouse.orders;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {
    private UUID orderId;
    private OrderStatus status;
    private String deliveryAddress;
    private ZonedDateTime createdAt;
    private List<OrderProductResponse> products;
    private BigDecimal totalPrice;
}

