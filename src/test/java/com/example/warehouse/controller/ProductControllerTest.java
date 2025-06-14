package com.example.warehouse.controller;

import com.example.warehouse.controller.ProductController;
import com.example.warehouse.controller.response.ProductResponse;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты для ProductController.
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ProductService productService() {
            return mock(ProductService.class);
        }
    }

    @Test
    void whenGetExistingProduct_thenReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        ProductResponse response = ProductResponse.builder().id(id).build();

        when(productService.getById(id)).thenReturn(response);

        mockMvc.perform(get("/api/products/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void whenGetNonExistingProduct_thenReturn404() throws Exception {
        UUID id = UUID.randomUUID();

        when(productService.getById(id)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/products/" + id))
                .andExpect(status().isNotFound());
    }
}