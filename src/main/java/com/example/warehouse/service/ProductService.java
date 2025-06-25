package com.example.warehouse.service;

import com.example.warehouse.controller.request.ProductCreateRequest;
import com.example.warehouse.controller.request.ProductUpdateRequest;
import com.example.warehouse.controller.response.ProductResponse;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.search.criteria.SearchCriteria;
import com.example.warehouse.service.impl.ProductServiceImpl;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис для бизнес-логики работы с товарами.
 * <p>
 * Содержит основные операции CRUD для управления товарами.
 * </p>
 *
 * @see ProductServiceImpl
 */
@Tag(name = "Product Service", description = "API для операций с товарами")
public interface ProductService {

    /**
     * Создает новый товар.
     *
     * @param request DTO с данными для создания товара
     * @return DTO созданного товара
     * @throws IllegalArgumentException если данные запроса невалидны
     */
    @Operation(summary = "Создать новый товар")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товар успешно создан"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные товара"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    ProductResponse create(ProductCreateRequest request);

    /**
     * Получает товар по идентификатору.
     *
     * @param id UUID товара
     * @return DTO товара
     * @throws ResourceNotFoundException если товар не найден
     */
    @Operation(summary = "Получить товар по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товар найден"),
            @ApiResponse(responseCode = "404", description = "Товар не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    ProductResponse getById(UUID id);

    /**
     * Обновляет существующий товар.
     *
     * @param id UUID товара для обновления
     * @param request DTO с данными для обновления
     * @return обновленный DTO товара
     * @throws ResourceNotFoundException если товар не найден
     * @throws IllegalArgumentException если данные запроса невалидны
     */
    @Operation(summary = "Обновить товар")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товар успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные товара"),
            @ApiResponse(responseCode = "404", description = "Товар не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    ProductResponse update(UUID id, ProductUpdateRequest request);

    /**
     * Удаляет товар по идентификатору.
     *
     * @param id UUID товара для удаления
     * @throws ResourceNotFoundException если товар не найден
     */
    @Operation(summary = "Удалить товар")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Товар успешно удален"),
            @ApiResponse(responseCode = "404", description = "Товар не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })

    @Transactional
    void delete(UUID id);

    /**
     * Получает список всех товаров.
     *
     * @return список DTO товаров
     */
    @Operation(summary = "Получить все товары")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список товаров получен"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    List<ProductResponse> getAll();

    /**
     * Ищет товары по заданным критериям
     * @param criteria список критериев поиска
     * @param page номер страницы
     * @param size размер страницы
     * @return страница с найденными товарами
     */
    Page<ProductResponse> searchProducts(List<SearchCriteria> criteria, int page, int size);
}
