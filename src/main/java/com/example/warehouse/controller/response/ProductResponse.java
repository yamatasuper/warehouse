package com.example.warehouse.controller.response;

import com.example.warehouse.entity.ProductCategory;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для ответа с информацией о товаре.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private UUID id;
    private String name;
    private String article;
    private String description;
    private ProductCategory category;
    private BigDecimal price;
    private BigDecimal quantity;
    private ZonedDateTime lastQuantityChange;
    private ZonedDateTime createdAt;
}
