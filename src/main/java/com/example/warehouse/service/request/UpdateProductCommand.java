package com.example.warehouse.service.request;

import com.example.warehouse.enums.ProductCategoryEnum;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Команда для обновления существующего товара.
 * Используется как DTO для передачи данных при обновлении товара.
 * Не содержит артикула, так как он не подлежит изменению после создания.
 */
@Getter
@Setter
@Builder
public class UpdateProductCommand {
    /**
     * Уникальный идентификатор обновляемого товара (обязательное поле)
     */
    private UUID id;

    /**
     * Новое наименование товара
     */
    private String name;

    /**
     * Новое описание товара
     */
    private String description;

    /**
     * Новая категория товара
     */
    private ProductCategoryEnum category;

    /**
     * Новая цена товара
     */
    private BigDecimal price;

    /**
     * Новое количество товара на складе
     */
    private BigDecimal quantity;
}