package com.example.warehouse.orders;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemRequest {
    @NotNull
    private UUID id;

    @DecimalMin("0.01")
    private BigDecimal quantity;
}
