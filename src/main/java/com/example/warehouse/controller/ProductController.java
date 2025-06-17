package com.example.warehouse.controller;

import com.example.warehouse.controller.request.ProductCreateRequest;
import com.example.warehouse.controller.request.ProductUpdateRequest;
import com.example.warehouse.controller.response.ProductResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@Tag(name = "Управление товарами", description = "CRUD операции для работы с товарами на складе")
public interface ProductController {

    @PostMapping
    @Operation(summary = "Создать товар", description = "Добавляет новый товар на склад")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товар успешно создан"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные товара"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductCreateRequest request);

    @GetMapping("/{id}")
    @Operation(summary = "Получить товар по ID", description = "Возвращает информацию о товаре по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товар найден"),
            @ApiResponse(responseCode = "404", description = "Товар не найден"),
            @ApiResponse(responseCode = "400", description = "Невалидный идентификатор товара")
    })
    ResponseEntity<ProductResponse> getById(@PathVariable UUID id);

    @PutMapping("/{id}")
    @Operation(summary = "Обновить товар", description = "Обновляет информацию о существующем товаре")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товар успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Товар не найден"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные товара")
    })
    ResponseEntity<ProductResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProductUpdateRequest request);

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить товар", description = "Удаляет товар с склада по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Товар успешно удален"),
            @ApiResponse(responseCode = "404", description = "Товар не найден"),
            @ApiResponse(responseCode = "400", description = "Невалидный идентификатор товара")
    })
    ResponseEntity<Void> delete(@PathVariable UUID id);

    @GetMapping
    @Operation(summary = "Получить все товары", description = "Возвращает список всех товаров на складе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список товаров успешно получен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    ResponseEntity<List<ProductResponse>> getAll();
}
