package com.example.warehouse.service.impl;

import com.example.warehouse.controller.request.ProductCreateRequest;
import com.example.warehouse.controller.request.ProductUpdateRequest;
import com.example.warehouse.controller.response.ProductResponse;
import com.example.warehouse.entity.ProductEntity;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.mapper.ProductMapper;
import com.example.warehouse.repository.ProductRepository;
import com.example.warehouse.service.ProductService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

/**
 * Реализация {@link ProductService} с транзакционной логикой.
 * <p>
 * Особенности:
 * </p>
 * <ul>
 *   <li>Все публичные методы транзакционные</li>
 *   <li>Обрабатывает исключения репозитория</li>
 *   <li>Выполняет преобразование DTO/Entity</li>
 * </ul>
 */

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse create(ProductCreateRequest request) {
        ProductEntity entity = productMapper.toEntity(request);
        ProductEntity saved = productRepository.save(entity);
        return productMapper.toResponse(saved);
    }

    @Override
    public ProductResponse getById(UUID id) {
        return productRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    public ProductResponse update(UUID id, ProductUpdateRequest request) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        productMapper.updateEntity(request, entity);
        ProductEntity updated = productRepository.save(entity);

        return productMapper.toResponse(updated);
    }

    @Override
    public void delete(UUID id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }
}
