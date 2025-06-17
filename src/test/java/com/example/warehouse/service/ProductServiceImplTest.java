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

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImplTest.class);

    @Mock
    private ProductRepository productRepositoryMock;

    @Mock
    private ProductMapper productMapperMock;

    @InjectMocks
    private ProductServiceImpl productServiceUnderTest;

    @Test
    void whenUpdateProduct_thenSaveUpdatedEntity() {
        // Arrange
        UUID id = UUID.randomUUID();
        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name("Updated Name")
                .price(BigDecimal.valueOf(20.0))
                .build();

        ProductEntity existingEntity = ProductEntity.builder()
                .id(id)
                .name("Original Name")
                .build();

        ProductEntity expectedSavedEntity = ProductEntity.builder()
                .id(id)
                .name("Updated Name")
                .build();

        logger.info("Starting test: whenUpdateProduct_thenSaveUpdatedEntity with ID: {}", id);

        when(productRepositoryMock.findById(id)).thenReturn(Optional.of(existingEntity));
        when(productRepositoryMock.save(existingEntity)).thenReturn(expectedSavedEntity);

        // Act
        productServiceUnderTest.update(id, request);

        // Assert
        verify(productMapperMock).updateEntity(request, existingEntity);
        verify(productRepositoryMock).save(existingEntity);
        logger.info("Test completed successfully - product with ID {} was updated", id);
    }

    @Test
    void whenUpdateNonExistingProduct_thenThrowException() {
        // Arrange
        UUID id = UUID.randomUUID();
        logger.info("Starting test: whenUpdateNonExistingProduct_thenThrowException with ID: {}", id);

        when(productRepositoryMock.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> productServiceUnderTest.update(id, new ProductUpdateRequest()));
        logger.info("Test completed successfully - ResourceNotFoundException thrown for non-existing ID: {}", id);
    }

    @Test
    void whenCreateProduct_thenReturnSavedEntity() {
        // Arrange
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("New Product")
                .price(BigDecimal.valueOf(15.99))
                .build();

        ProductEntity newEntity = ProductEntity.builder()
                .name("New Product")
                .build();

        ProductEntity savedEntity = ProductEntity.builder()
                .id(UUID.randomUUID())
                .name("New Product")
                .build();

        ProductResponse expectedResponse = ProductResponse.builder()
                .id(savedEntity.getId())
                .name("New Product")
                .build();

        logger.info("Starting test: whenCreateProduct_thenReturnSavedEntity");

        when(productMapperMock.toEntity(request)).thenReturn(newEntity);
        when(productRepositoryMock.save(newEntity)).thenReturn(savedEntity);
        when(productMapperMock.toResponse(savedEntity)).thenReturn(expectedResponse);

        // Act
        ProductResponse actualResponse = productServiceUnderTest.create(request);

        // Assert
        assertSame(expectedResponse, actualResponse);
        logger.info("Test completed successfully - new product created with response: {}", actualResponse);
    }
}