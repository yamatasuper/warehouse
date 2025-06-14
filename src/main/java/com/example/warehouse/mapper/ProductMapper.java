package com.example.warehouse.mapper;

import com.example.warehouse.controller.request.ProductCreateRequest;
import com.example.warehouse.controller.request.ProductUpdateRequest;
import com.example.warehouse.controller.response.ProductResponse;
import com.example.warehouse.entity.ProductEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Маппер для преобразования между DTO и сущностями товаров.
 * <p>
 * Использует MapStruct для генерации реализации.
 * </p>
 *
 * @see <a href="https://mapstruct.org/">MapStruct Documentation</a>
 */

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lastQuantityChange", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ProductEntity toEntity(ProductCreateRequest request);

    @Mapping(target = "article", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastQuantityChange", expression = "java(java.time.ZonedDateTime.now())")
    void updateEntity(ProductUpdateRequest request, @MappingTarget ProductEntity entity);

    ProductResponse toResponse(ProductEntity entity);
}
