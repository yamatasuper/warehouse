package com.example.warehouse.service.request;

import com.example.warehouse.enums.ProductCategoryEnum;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Команда для создания нового товара.
 * Используется как DTO для передачи данных при создании товара.
 */
@Getter
@Setter
@Builder
public class CreateProductCommand {
    /**
     * Уникальный идентификатор товара (генерируется автоматически, если не указан)
     */
    private UUID id;

    /**
     * Наименование товара (обязательное поле)
     */
    private String name;

    /**
     * Уникальный артикул товара (обязательное поле)
     */
    private String article;

    /**
     * Подробное описание товара
     */
    private String description;

    /**
     * Категория товара (обязательное поле)
     */
    private ProductCategoryEnum category;

    /**
     * Цена товара в валюте системы (обязательное поле)
     */
    private BigDecimal price;

    /**
     * Начальное количество товара на складе (обязательное поле)
     */
    private BigDecimal quantity;
}
