package com.example.warehouse.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.warehouse.controller.mapper.ProductDtoMapper;
import com.example.warehouse.controller.request.ProductCreateRequest;
import com.example.warehouse.controller.request.ProductUpdateRequest;
import com.example.warehouse.controller.response.ProductResponse;
import com.example.warehouse.enums.ProductCategoryEnum;
import com.example.warehouse.persistence.entity.ProductEntity;
import com.example.warehouse.persistence.repository.ProductRepository;
import com.example.warehouse.search.criteria.SearchCriteriaValidator;
import com.example.warehouse.service.impl.ProductServiceImpl;
import com.example.warehouse.service.model.Product;
import com.example.warehouse.service.request.CreateProductCommand;
import com.example.warehouse.service.request.UpdateProductCommand;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductServiceMapper serviceMapper;
    @Mock
    private ProductDtoMapper dtoMapper;
    @Mock
    private SearchCriteriaValidator searchCriteriaValidator;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void whenCreateProduct_thenReturnId() {
        ProductCreateRequest request = new ProductCreateRequest("Test", "TEST-123", null, null, null, null);
        ProductEntity entity = new ProductEntity();
        entity.setId(UUID.randomUUID());

        when(dtoMapper.toEntity(request)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(entity);

        UUID result = productService.create(request);

        assertEquals(entity.getId(), result);
        verify(productRepository).save(entity);
    }


    /**
     * Модель товара в системе.
     * Содержит полную информацию о товаре, включая идентификатор, описание,
     * категорию, цену и количество на складе.
     */
    public record Product(
            UUID id,
            String name,
            String article,
            String description,
            ProductCategoryEnum category,
            BigDecimal price,
            BigDecimal quantity,
            OffsetDateTime lastQuantityChange,
            LocalDateTime createdAt
    ) {}
}