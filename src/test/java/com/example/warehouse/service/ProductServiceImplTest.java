package com.example.warehouse.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.warehouse.controller.request.ProductCreateRequest;
import com.example.warehouse.controller.request.ProductUpdateRequest;
import com.example.warehouse.controller.response.ProductResponse;
import com.example.warehouse.entity.ProductEntity;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.mapper.ProductMapper;
import com.example.warehouse.repository.ProductRepository;
import com.example.warehouse.service.impl.ProductServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Тесты для ProductServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImplTest.class);

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void whenUpdateProduct_thenSaveUpdatedEntity() {
        UUID id = UUID.randomUUID();
        ProductUpdateRequest request = new ProductUpdateRequest();
        ProductEntity entity = new ProductEntity();
        ProductEntity updatedEntity = new ProductEntity();
        logger.info("Starting test: whenUpdateProduct_thenSaveUpdatedEntity with ID: {}", id);

        when(productRepository.findById(id)).thenReturn(Optional.of(entity));
        when(productRepository.save(entity)).thenReturn(updatedEntity);

        productService.update(id, request);

        verify(productMapper).updateEntity(request, entity);
        verify(productRepository).save(entity);
        logger.info("Test completed successfully - product with ID {} was updated", id);
    }

    @Test
    void whenUpdateNonExistingProduct_thenThrowException() {
        UUID id = UUID.randomUUID();
        logger.info("Starting test: whenUpdateNonExistingProduct_thenThrowException with ID: {}", id);

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.update(id, new ProductUpdateRequest()));
        logger.info("Test completed successfully - ResourceNotFoundException thrown for non-existing ID: {}", id);
    }

    @Test
    void whenCreateProduct_thenReturnSavedEntity() {
        ProductCreateRequest request = new ProductCreateRequest();
        ProductEntity entity = new ProductEntity();
        ProductEntity savedEntity = new ProductEntity();
        ProductResponse response = new ProductResponse();
        logger.info("Starting test: whenCreateProduct_thenReturnSavedEntity");

        when(productMapper.toEntity(request)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(savedEntity);
        when(productMapper.toResponse(savedEntity)).thenReturn(response);

        ProductResponse result = productService.create(request);

        assertSame(response, result);
        logger.info("Test completed successfully - new product created with response: {}", response);
    }
}