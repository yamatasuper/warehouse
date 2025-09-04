package com.example.warehouse.repository;

import com.example.warehouse.persistence.entity.ProductEntity;
import com.example.warehouse.persistence.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @Sql(scripts = "/scripts/insert-single-product-before.sql")
    void whenFindByArticle_thenReturnCorrectResult() {
        // when
        boolean exists = productRepository.existsByArticle("TEST1");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void whenFindByNonExistingArticle_thenReturnFalse() {
        // when - база пустая благодаря @DataJpaTest
        boolean exists = productRepository.existsByArticle("NONEXISTENT");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @Sql(scripts = "/scripts/insert-multiple-products-before.sql")
    void whenFindMultipleProducts_thenReturnCorrectCount() {
        // when
        boolean existsCheap = productRepository.existsByArticle("CHEAP1");
        boolean existsExpensive1 = productRepository.existsByArticle("EXPENSIVE1");
        boolean existsExpensive2 = productRepository.existsByArticle("EXPENSIVE2");

        // then
        assertThat(existsCheap).isTrue();
        assertThat(existsExpensive1).isTrue();
        assertThat(existsExpensive2).isTrue();
    }

    // Дополнительный тест для проверки пагинации
    @Test
    @Sql(scripts = "/scripts/insert-multiple-products-before.sql")
    void whenFindProductsForUpdateWithPagination_thenReturnCorrectNumberOfProducts() {
        // when
        List<ProductEntity> firstPage = productRepository.findProductsForUpdate(0, 2);
        List<ProductEntity> secondPage = productRepository.findProductsForUpdate(2, 2);

        // then
        assertThat(firstPage).hasSize(2);
        assertThat(secondPage).hasSize(1);
    }
}