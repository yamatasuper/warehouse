package com.example.warehouse.service;

import com.example.warehouse.controller.request.ProductCreateRequest;
import com.example.warehouse.controller.request.ProductUpdateRequest;
import com.example.warehouse.controller.response.ProductResponse;

import java.util.List;
import java.util.UUID;

/**
 * Сервис для бизнес-логики работы с товарами.
 * <p>
 * Содержит основные операции:
 * </p>
 * <ul>
 *   <li>Создание и валидация товаров</li>
 *   <li>Транзакционные операции</li>
 *   <li>Обработка бизнес-правил</li>
 * </ul>
 */

public interface ProductService {
    ProductResponse create(ProductCreateRequest request);
    ProductResponse getById(UUID id);
    ProductResponse update(UUID id, ProductUpdateRequest request);
    void delete(UUID id);
    List<ProductResponse> getAll();
}
