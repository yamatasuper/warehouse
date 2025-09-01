//package com.example.warehouse.service;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertSame;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.example.warehouse.controller.mapper.ProductDtoMapper;
//import com.example.warehouse.controller.request.ProductCreateRequest;
//import com.example.warehouse.controller.request.ProductUpdateRequest;
//import com.example.warehouse.controller.response.ProductResponse;
//import com.example.warehouse.persistence.entity.ProductEntity;
//import com.example.warehouse.persistence.repository.ProductRepository;
//import com.example.warehouse.search.criteria.SearchCriteriaValidator;
//import com.example.warehouse.service.impl.ProductServiceImpl;
//import com.example.warehouse.service.model.Product;
//import com.example.warehouse.service.request.CreateProductCommand;
//import com.example.warehouse.service.request.UpdateProductCommand;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.util.Optional;
//import java.util.UUID;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@ExtendWith(MockitoExtension.class)
//class ProductServiceImplTest {
//    @Mock
//    private ProductRepository productRepository;
//    @Mock
//    private ProductServiceMapper serviceMapper;
//    @Mock
//    private ProductDtoMapper dtoMapper;
//    @Mock
//    private SearchCriteriaValidator searchCriteriaValidator;
//
//    @InjectMocks
//    private ProductServiceImpl productService;
//
//    @Test
//    void whenCreateProduct_thenReturnId() {
//        ProductCreateRequest request = new ProductCreateRequest("Test", "TEST-123", null, null, null, null);
//        ProductEntity entity = new ProductEntity();
//        entity.setId(UUID.randomUUID());
//
//        when(dtoMapper.toEntity(request)).thenReturn(entity);
//        when(productRepository.save(entity)).thenReturn(entity);
//
//        UUID result = productService.create(request);
//
//        assertEquals(entity.getId(), result);
//        verify(productRepository).save(entity);
//    }
//
//    @Test
//    void whenUpdateProduct_thenReturnUpdatedResponse() {
//        UUID id = UUID.randomUUID();
//        ProductUpdateRequest request = new ProductUpdateRequest("Updated", "UPD-123", null, null, null, null);
//        ProductEntity entity = new ProductEntity();
//        Product domain = new Product(id, "Updated", "UPD-123", null, null, null, null, null, null);
//        ProductResponse response = new ProductResponse(id, "Updated", "UPD-123", null, null, null, null, null, null);
//
//        when(productRepository.findById(id)).thenReturn(Optional.of(entity));
//        when(serviceMapper.toDomain(entity)).thenReturn(domain);
//        when(dtoMapper.toResponse(domain)).thenReturn(response);
//
//        ProductResponse result = productService.update(id, request);
//
//        assertEquals(response, result);
//        verify(dtoMapper).updateEntity(entity, request);
//        verify(productRepository).save(entity);
//    }
//}