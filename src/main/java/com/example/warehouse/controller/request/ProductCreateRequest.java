package com.example.warehouse.controller.request;

import com.example.warehouse.enums.ProductCategoryEnum;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
public record ProductCreateRequest(
        @NotBlank String name,
        @NotBlank String article,
        String description,
        @NotNull ProductCategoryEnum category,
        @PositiveOrZero BigDecimal price,
        @DecimalMin("0.00") BigDecimal quantity
) {}