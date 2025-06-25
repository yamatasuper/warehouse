package com.example.warehouse.service;

import com.example.warehouse.persistence.entity.ProductEntity;
import com.example.warehouse.service.model.Product;
import com.example.warehouse.service.request.CreateProductCommand;
import com.example.warehouse.service.request.UpdateProductCommand;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.ZonedDateTime;

/**
 * Маппер для преобразования между сущностями продукта, DTO и доменными моделями.
 * <p>
 * Использует MapStruct для автоматической генерации кода преобразований.
 * Интегрируется с Spring и обрабатывает особые случаи, такие как генерация временных меток.
 * </p>
 */
@Mapper(
        componentModel = "spring",
        imports = {ZonedDateTime.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductServiceMapper {

    /**
     * Преобразует команду создания в новую сущность продукта.
     * <p>
     * Игнорирует ID (должен генерироваться БД) и временные метки
     * (устанавливаются при сохранении).
     * </p>
     *
     * @param command DTO с данными для создания продукта
     * @return новая сущность продукта
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lastQuantityChange", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ProductEntity toEntity(CreateProductCommand command);

    /**
     * Обновляет существующую сущность продукта из команды обновления.
     * <p>
     * Сохраняет артикул и дату создания без изменений.
     * Автоматически устанавливает текущее время для lastQuantityChange.
     * </p>
     *
     * @param command DTO с новыми значениями
     * @param entity целевая сущность для обновления
     */
    @Mapping(target = "article", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastQuantityChange", expression = "java(ZonedDateTime.now())")
    void updateEntity(UpdateProductCommand command, @MappingTarget ProductEntity entity);

    /**
     * Частичное обновление сущности продукта (только не-null поля).
     * <p>
     * Не изменяет неизменяемые поля (артикул, ID, дата создания).
     * Не обновляет временную метку lastQuantityChange.
     * Игнорирует null-значения в команде обновления.
     * </p>
     *
     * @param command DTO с новыми значениями
     * @param entity целевая сущность для обновления
     */
    @Mapping(target = "article", ignore = true)
    @Mapping(target = "lastQuantityChange", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(UpdateProductCommand command, @MappingTarget ProductEntity entity);

    /**
     * Преобразует сущность продукта в доменную модель.
     *
     * @param entity сущность продукта
     * @return доменная модель продукта
     */
    Product toDomain(ProductEntity entity);
}