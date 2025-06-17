package com.example.warehouse.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Категории товаров.
 * <p>
 * Используется для классификации товаров на складе.
 * </p>
 */
@Schema(
        description = "Категория товара",
        enumAsRef = true,
        allowableValues = {
                "ELECTRONICS",
                "CLOTHING",
                "FOOD",
                "FURNITURE",
                "OTHER"
        }
)
public enum ProductCategoryEnum {
    @Schema(description = "Электроника и гаджеты")
    ELECTRONICS,

    @Schema(description = "Одежда и аксессуары")
    CLOTHING,

    @Schema(description = "Продукты питания")
    FOOD,

    @Schema(description = "Мебель и предметы интерьера")
    FURNITURE,

    @Schema(description = "Другие категории товаров")
    OTHER
}
