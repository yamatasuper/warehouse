package com.example.warehouse.entity;
import com.example.warehouse.enums.ProductCategoryEnum;

import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Сущность товара для хранения в базе данных.
 * <p>
 * Соответствует таблице 'products' в БД. Содержит все основные характеристики товара,
 * включая цену, количество и историю изменений.
 * </p>
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Сущность товара в базе данных")
public class ProductEntity {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    @Schema(description = "Уникальный идентификатор товара",
            example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    @Schema(description = "Наименование товара",
            example = "Смартфон Samsung Galaxy S23",
            required = true)
    private String name;

    @NotBlank
    @Column(unique = true, nullable = false)
    @Schema(description = "Уникальный артикул товара",
            example = "SM-S911BZKDSEK",
            required = true)
    private String article;

    @Schema(description = "Подробное описание товара",
            example = "Флагманский смартфон с динамическим AMOLED 2X экраном 6.1\"")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255)")
    @Schema(description = "Категория товара",
            implementation = ProductCategoryEnum.class,
            required = true)
    private ProductCategoryEnum category;

    @Column(nullable = false, precision = 19, scale = 2)
    @Schema(description = "Цена товара",
            example = "799.99",
            required = true)
    @PositiveOrZero
    private BigDecimal price;

    @Column(nullable = false, precision = 19, scale = 2)
    @Schema(description = "Количество товара на складе",
            example = "25.50",
            required = true)
    private BigDecimal quantity;

    @CreationTimestamp
    @Column(name = "last_quantity_change", nullable = false)
    @Schema(description = "Дата и время последнего изменения количества",
            example = "2023-06-15T14:30:45+03:00")
    private ZonedDateTime lastQuantityChange;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Дата и время создания записи о товаре",
            example = "2023-06-10T09:15:22+03:00")
    private ZonedDateTime createdAt;
}