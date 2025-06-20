package com.example.warehouse.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.warehouse.entity.ProductEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @SqlGroup({
            @Sql(scripts = "/scripts/insert-products-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "/scripts/cleanup-products-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    void whenFindByArticle_thenReturnCorrectResult() {
        // when
        boolean exists = productRepository.existsByArticle("TEST123");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @Sql(scripts = "/scripts/cleanup-products-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void whenFindByNonExistingArticle_thenReturnFalse() {
        // when
        boolean exists = productRepository.existsByArticle("NONEXISTENT");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "/scripts/insert-multiple-products-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "/scripts/cleanup-products-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    void whenFindExpensiveProducts_thenReturnCorrectProducts() {
        // when
        List<ProductEntity> expensiveProducts = productRepository.findExpensiveProducts(BigDecimal.valueOf(100));

        // then
        assertThat(expensiveProducts).hasSize(2);
        assertThat(expensiveProducts)
                .extracting(ProductEntity::getArticle)
                .containsExactlyInAnyOrder("EXPENSIVE1", "EXPENSIVE2");
    }

    @Test
    @SqlGroup({
            @Sql(scripts = "/scripts/insert-single-product-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts = "/scripts/cleanup-products-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    void whenFindExpensiveProductsWithHighThreshold_thenReturnEmptyList() {
        // when
        List<ProductEntity> expensiveProducts = productRepository.findExpensiveProducts(BigDecimal.valueOf(1000));

        // then
        assertThat(expensiveProducts).isEmpty();
    }
}