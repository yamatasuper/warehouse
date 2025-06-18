package com.example.warehouse.service.model;

import com.example.warehouse.enums.ProductCategoryEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Модель товара в системе.
 * Содержит полную информацию о товаре, включая идентификатор, описание,
 * категорию, цену и количество на складе.
 */
@Getter
@Setter
@Builder
public class Product {
    /**
     * Уникальный идентификатор товара
     */
    private UUID id;

    /**
     * Наименование товара
     */
    private String name;

    /**
     * Уникальный артикул товара
     */
    private String article;

    /**
     * Подробное описание товара
     */
    private String description;

    /**
     * Категория товара
     */
    private ProductCategoryEnum category;

    /**
     * Цена товара в валюте системы
     */
    private BigDecimal price;

    /**
     * Количество товара на складе
     */
    private BigDecimal quantity;

    /**
     * Дата и время последнего изменения количества
     */
    private ZonedDateTime lastQuantityChange;

    /**
     * Дата создания товара в системе
     */
    private LocalDate createdAt;
}
