package com.example.warehouse.mapper;

import com.example.warehouse.controller.request.ProductCreateRequest;
import com.example.warehouse.controller.request.ProductUpdateRequest;
import com.example.warehouse.controller.response.ProductResponse;
import com.example.warehouse.entity.ProductEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.mapstruct.*;

import java.time.ZonedDateTime;

/**
 * Маппер для преобразования между DTO и сущностями товаров.
 * <p>
 * Использует MapStruct для генерации реализации. Автоматически генерирует реализацию
 * на этапе компиляции, обеспечивая высокую производительность.
 * </p>
 *
 * @see <a href="https://mapstruct.org/">MapStruct Documentation</a>
 */
@Mapper(
        componentModel = "spring",
        imports = {ZonedDateTime.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
@Schema(description = "Маппер для преобразования между DTO и сущностями товаров")
public interface ProductMapper {

    /**
     * Преобразует ProductCreateRequest в ProductEntity.
     * <p>
     * Игнорирует поля id, lastQuantityChange и createdAt, так как они устанавливаются на уровне сервиса.
     * </p>
     *
     * @param request DTO для создания товара
     * @return сущность товара
     */
    @Operation(summary = "Преобразование DTO создания в сущность")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lastQuantityChange", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Schema(description = "Преобразует DTO создания товара в сущность")
    ProductEntity toEntity(@Schema(description = "DTO для создания товара") ProductCreateRequest request);

    /**
     * Обновляет существующую сущность товара на основе данных из ProductUpdateRequest.
     * <p>
     * Устанавливает lastQuantityChange на текущее время, игнорирует article, id и createdAt.
     * </p>
     *
     * @param request DTO для обновления товара
     * @param entity целевая сущность для обновления
     */
    @Operation(summary = "Обновление сущности товара из DTO")
    @Mapping(target = "article", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastQuantityChange", expression = "java(ZonedDateTime.now())")
    @Schema(description = "Обновляет сущность товара из DTO обновления")
    void updateEntity(
            @Schema(description = "DTO для обновления товара") ProductUpdateRequest request,
            @Schema(description = "Сущность товара для обновления") @MappingTarget ProductEntity entity
    );

    /**
     * Преобразует ProductEntity в ProductResponse.
     *
     * @param entity сущность товара
     * @return DTO ответа с информацией о товаре
     */
    @Operation(summary = "Преобразование сущности в DTO ответа")
    @Schema(description = "Преобразует сущность товара в DTO ответа")
    ProductResponse toResponse(@Schema(description = "Сущность товара") ProductEntity entity);

    /**
     * Частично обновляет сущность товара, игнорируя null значения.
     *
     * @param request DTO для частичного обновления
     * @param entity целевая сущность для обновления
     */
    @Operation(summary = "Частичное обновление сущности товара")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Schema(description = "Частично обновляет сущность товара, игнорируя null значения")
    void partialUpdate(
            @Schema(description = "DTO для частичного обновления") ProductUpdateRequest request,
            @Schema(description = "Сущность товара для обновления") @MappingTarget ProductEntity entity
    );
}