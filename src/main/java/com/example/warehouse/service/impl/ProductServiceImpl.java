package com.example.warehouse.service.impl;

import com.example.warehouse.controller.request.ProductCreateRequest;
import com.example.warehouse.controller.request.ProductUpdateRequest;
import com.example.warehouse.controller.response.ProductResponse;
import com.example.warehouse.entity.ProductEntity;
import com.example.warehouse.exception.DeleteResourceException;
import com.example.warehouse.exception.DuplicateResourceException;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.mapper.ProductMapper;
import com.example.warehouse.repository.ProductRepository;
import com.example.warehouse.service.ProductService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация {@link ProductService} с транзакционной логикой.
 * <p>
 * Особенности реализации:
 * </p>
 * <ul>
 *   <li>Все публичные методы имеют транзакционное поведение</li>
 *   <li>Обрабатывает исключения уровня репозитория</li>
 *   <li>Выполняет преобразование между DTO и Entity</li>
 *   <li>Гарантирует целостность данных при операциях</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ProductResponse create(ProductCreateRequest request) {
        // Проверка на дубликат по артикулу
        if (productRepository.existsByArticle(request.getArticle())) {
            throw new DuplicateResourceException("Product with article " + request.getArticle() + " already exists");
        }

        ProductEntity entity = productMapper.toEntity(request);
        ProductEntity saved = productRepository.save(entity);
        return productMapper.toResponse(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(UUID id) {
        return productRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ProductResponse update(UUID id, ProductUpdateRequest request) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        // Проверка на дубликат по артикулу при обновлении
        if (request.getArticle() != null &&
                !request.getArticle().equals(entity.getArticle()) &&
                productRepository.existsByArticle(request.getArticle())) {
            throw new DuplicateResourceException("Product with article " + request.getArticle() + " already exists");
        }

        productMapper.updateEntity(request, entity);
        ProductEntity updated = productRepository.save(entity);

        return productMapper.toResponse(updated);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void delete(UUID id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        productRepository.delete(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }
}