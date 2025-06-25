package com.example.warehouse.controller;

import com.example.warehouse.controller.mapper.ProductDtoMapper;
import com.example.warehouse.controller.request.ProductCreateRequest;
import com.example.warehouse.controller.request.ProductUpdateRequest;
import com.example.warehouse.controller.response.ProductResponse;
import com.example.warehouse.search.criteria.SearchCriteria;
import com.example.warehouse.service.ProductService;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST контроллер для управления товарами на складе.
 * <p>
 * Предоставляет полный набор CRUD операций:
 * </p>
 * <ul>
 *   <li>Создание, чтение, обновление, удаление товаров</li>
 *   <li>Фильтрация и поиск</li>
 *   <li>Валидация входящих данных</li>
 * </ul>
 *
 * @see ProductService
 * @see ProductResponse
 */
@RestController
@RequiredArgsConstructor
public class ProductControllerImpl implements ProductController {

    private final ProductService productService;
    private final ProductDtoMapper dtoMapper;

    @Override
    public ResponseEntity<IdResponse> create(@Valid @RequestBody ProductCreateRequest request) {
        UUID id = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(id));
    }

    @Override
    public ProductResponse getById(@PathVariable UUID id) {
        return productService.getById(id);
    }

    @Override
    public ProductResponse update(@PathVariable UUID id, @Valid @RequestBody ProductUpdateRequest request) {
        return productService.update(id, request);
    }

    @Override
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public List<ProductResponse> getAll() {
        return productService.getAll();
    }

    @Override
    public Page<ProductResponse> searchProducts(
            @RequestBody @Valid List<SearchCriteria> criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.searchProducts(criteria, page, size);
    }
}

