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

/**
 * Тесты для ProductServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
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

        when(productRepository.findById(id)).thenReturn(Optional.of(entity));
        when(productRepository.save(entity)).thenReturn(updatedEntity);

        productService.update(id, request);

        verify(productMapper).updateEntity(request, entity);
        verify(productRepository).save(entity);
    }

    @Test
    void whenUpdateNonExistingProduct_thenThrowException() {
        UUID id = UUID.randomUUID();

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.update(id, new ProductUpdateRequest()));
    }

    @Test
    void whenCreateProduct_thenReturnSavedEntity() {
        ProductCreateRequest request = new ProductCreateRequest();
        ProductEntity entity = new ProductEntity();
        ProductEntity savedEntity = new ProductEntity();
        ProductResponse response = new ProductResponse();

        when(productMapper.toEntity(request)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(savedEntity);
        when(productMapper.toResponse(savedEntity)).thenReturn(response);

        ProductResponse result = productService.create(request);

        assertSame(response, result);
    }
}