package com.example.warehouse.controller.request;

import com.example.warehouse.entity.ProductCategory;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для запроса на создание товара.
 * <p>
 * Обязательные поля:
 * </p>
 * <ul>
 *   <li>name - название товара</li>
 *   <li>article - уникальный артикул</li>
 *   <li>category - категория товара</li>
 *   <li>price - цена (должна быть положительной)</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreateRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String article;

    private String description;

    @NotNull
    private ProductCategory category;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal quantity;
}
