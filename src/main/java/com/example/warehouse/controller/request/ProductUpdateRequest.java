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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Данные для обновления информации о товаре")
public class ProductUpdateRequest {
    @NotBlank
    @Schema(description = "Наименование товара", example = "Смартфон Samsung Galaxy S21 (обновленная версия)", required = true)
    private String name;

    @NotBlank
    @Schema(description = "Артикул товара", example = "SM-G991BZADSEK", required = true)
    private String article;

    @Schema(description = "Описание товара", example = "Флагманский смартфон с AMOLED-экраном 6.2\" и улучшенной камерой")
    private String description;

    @NotNull
    @Schema(description = "Категория товара", required = true)
    private ProductCategoryEnum category;

    @NotNull
    @DecimalMin("0.01")
    @Schema(description = "Цена товара (должна быть больше 0)", example = "950.00", required = true)
    @PositiveOrZero
    private BigDecimal price;

    @NotNull
    @DecimalMin("0.00")
    @Schema(description = "Количество товара (не может быть отрицательным)", example = "15.00", required = true)
    private BigDecimal quantity;
}