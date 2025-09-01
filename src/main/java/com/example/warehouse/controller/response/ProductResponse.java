package com.example.warehouse.controller.response;

import com.example.warehouse.enums.ProductCategoryEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для ответа с информацией о товаре.
 * <p>
 * Содержит полную информацию о товаре, включая системные поля (ID, даты создания и изменения).
 * </p>
 */
@Data
@Builder
@Schema(description = "Полная информация о товаре")
@NotNull
public class ProductResponse {
    @Schema(description = "Уникальный идентификатор товара", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private final UUID id;

    @Schema(description = "Наименование товара", example = "Смартфон Samsung Galaxy S21")
    private final String name;

    @Schema(description = "Артикул товара (уникальный)", example = "SM-G991BZKDSEK")
    private final String article;

    @Schema(description = "Описание товара", example = "Флагманский смартфон с AMOLED-экраном 6.2\"")
    private final String description;

    @Schema(description = "Категория товара")
    private final ProductCategoryEnum category;

    @Schema(description = "Цена товара", example = "899.99")
    @PositiveOrZero
    private final BigDecimal price;

    @Schema(description = "Количество товара на складе", example = "15.00")
    private final BigDecimal quantity;

    @Schema(description = "Дата и время последнего изменения количества", example = "2023-05-15T14:30:45+03:00")
    private final OffsetDateTime lastQuantityChange;

    @Schema(description = "Дата и время создания записи о товаре", example = "2023-05-10T09:15:22+03:00")
    private final LocalDateTime createdAt;

    @Schema(description = "Валюта цены товара", example = "RUB")
    private final String currency;
}