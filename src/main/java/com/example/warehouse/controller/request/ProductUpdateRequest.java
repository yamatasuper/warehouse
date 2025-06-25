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
 * DTO для запроса на обновление товара.
 * <p>
 * Содержит все поля, которые могут быть обновлены у существующего товара.
 * Все поля обязательны для заполнения (частичные обновления не поддерживаются).
 * </p>
 */
public record ProductUpdateRequest(
        @NotBlank String name,
        @NotBlank String article,
        String description,
        @NotNull ProductCategoryEnum category,
        @PositiveOrZero BigDecimal price,
        @DecimalMin("0.00") BigDecimal quantity
) {}