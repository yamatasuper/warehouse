package com.example.warehouse.orders;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderProductResponse {
    private UUID productId;
    private String name;
    private BigDecimal quantity;
    private BigDecimal price;
}
