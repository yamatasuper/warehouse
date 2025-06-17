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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Данные для создания нового товара")
public class ProductCreateRequest {
    @NotBlank
    @Schema(description = "Наименование товара", example = "Смартфон Samsung Galaxy S21", required = true)
    private String name;

    @NotBlank
    @Schema(description = "Артикул товара (уникальный)", example = "SM-G991BZKDSEK", required = true)
    private String article;

    @Schema(description = "Описание товара", example = "Флагманский смартфон с AMOLED-экраном 6.2\"")
    private String description;

    @NotNull
    @Schema(description = "Категория товара", required = true)
    private ProductCategoryEnum category;

    @NotNull
    @DecimalMin("0.01")
    @Schema(description = "Цена товара (должна быть больше 0)", example = "899.99", required = true)
    @PositiveOrZero
    private BigDecimal price;

    @NotNull
    @DecimalMin("0.00")
    @Schema(description = "Количество товара (не может быть отрицательным)", example = "10.00", required = true)
    private BigDecimal quantity;
}