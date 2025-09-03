//package com.example.warehouse.controller;
//
//import com.example.warehouse.controller.mapper.ProductDtoMapper;
//import com.example.warehouse.controller.response.ProductResponse;
//import com.example.warehouse.exception.ResourceNotFoundException;
//import com.example.warehouse.service.ProductService;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.UUID;
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
///**
// * Тесты для ProductController.
// */
//class ProductControllerTest {
//    private static final Logger logger = LoggerFactory.getLogger(ProductControllerTest.class);
//
//    private MockMvc mockMvc;
//    private ProductService productService;
//    private ProductDtoMapper dtoMapper;
//
//    @BeforeEach
//    void setUp() {
//        productService = mock(ProductService.class);
//        dtoMapper = mock(ProductDtoMapper.class);
//        ProductController controller = new ProductControllerImpl(productService, dtoMapper);
//        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
//    }
//
//    @Test
//    void whenGetExistingProduct_thenReturn200() throws Exception {
//        UUID id = UUID.randomUUID();
//        ProductResponse response = new ProductResponse(id, "Test", "TEST-123", null, null, null, null, null, null);
//
//        when(productService.getById(id)).thenReturn(response);
//
//        mockMvc.perform(get("/api/products/" + id))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(id.toString()))
//                .andExpect(jsonPath("$.name").value("Test"))
//                .andExpect(jsonPath("$.article").value("TEST-123"));
//    }
//
//    @Test
//    void whenGetNonExistingProduct_thenReturn404() throws Exception {
//        UUID id = UUID.randomUUID();
//        when(productService.getById(id)).thenThrow(new ResourceNotFoundException(id));
//
//        mockMvc.perform(get("/api/products/" + id))
//                .andExpect(status().isNotFound());
//    }
//}
