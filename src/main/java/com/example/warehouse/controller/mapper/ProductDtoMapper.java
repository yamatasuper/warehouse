package com.example.warehouse.controller.mapper;

import com.example.warehouse.controller.request.ProductCreateRequest;
import com.example.warehouse.controller.request.ProductUpdateRequest;
import com.example.warehouse.controller.response.ProductResponse;
import com.example.warehouse.service.model.Product;
import com.example.warehouse.service.request.CreateProductCommand;
import com.example.warehouse.service.request.UpdateProductCommand;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductDtoMapper {

    // Преобразование DTO в команды сервиса
    @Mapping(target = "id", ignore = true)
    CreateProductCommand toCommand(ProductCreateRequest request);

    @Mapping(target = "id", ignore = true)
    UpdateProductCommand toCommand(ProductUpdateRequest request);

    // Преобразование модели сервиса в DTO ответа
    ProductResponse toResponse(Product product);
}
