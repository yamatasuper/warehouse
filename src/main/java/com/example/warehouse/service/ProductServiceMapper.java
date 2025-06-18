package com.example.warehouse.service;

import com.example.warehouse.entity.ProductEntity;
import com.example.warehouse.service.model.Product;
import com.example.warehouse.service.request.CreateProductCommand;
import com.example.warehouse.service.request.UpdateProductCommand;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.ZonedDateTime;

@Mapper(
        componentModel = "spring",
        imports = {ZonedDateTime.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductServiceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lastQuantityChange", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ProductEntity toEntity(CreateProductCommand command);

    @Mapping(target = "article", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastQuantityChange", expression = "java(ZonedDateTime.now())")
    void updateEntity(UpdateProductCommand command, @MappingTarget ProductEntity entity);

    @Mapping(target = "article", ignore = true)
    @Mapping(target = "lastQuantityChange", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(UpdateProductCommand command, @MappingTarget ProductEntity entity);

    Product toDomain(ProductEntity entity);
}
