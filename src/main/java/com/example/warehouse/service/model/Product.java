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
public record Product(
        UUID id,
        String name,
        String article,
        String description,
        ProductCategoryEnum category,
        BigDecimal price,
        BigDecimal quantity,
        ZonedDateTime lastQuantityChange,
        LocalDate createdAt
) {}
