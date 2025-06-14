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
 * DTO для запроса на обновление товара.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateRequest {
    @NotBlank
    private String name;

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
