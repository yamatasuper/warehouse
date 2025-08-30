package com.example.warehouse.service;

import com.example.warehouse.controller.request.ProductCreateRequest;
import com.example.warehouse.controller.request.ProductUpdateRequest;
import com.example.warehouse.controller.response.ProductResponse;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.persistence.entity.ProductEntity;
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
public interface ProductService {
    UUID create(ProductCreateRequest request);
    ProductResponse getById(UUID id);
    ProductResponse update(UUID id, ProductUpdateRequest request);
    void delete(UUID id);
    List<ProductResponse> getAll();
    Page<ProductResponse> searchProducts(List<SearchCriteria> criteria, int page, int size);
    List<ProductEntity> getAllEntities();
}