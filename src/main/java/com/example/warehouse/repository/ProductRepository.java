package com.example.warehouse.repository;

import com.example.warehouse.entity.ProductEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.LockModeType;

/**
 * Репозиторий для доступа к данным товаров.
 * <p>
 * Наследует стандартные CRUD операции от JpaRepository.
 * </p>
 *
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID>,
        JpaSpecificationExecutor<ProductEntity> {
    /**
     * Проверяет существование товара с указанным артикулом.
     *
     * @param article артикул товара для проверки
     * @return true - если товар с таким артикулом существует, false - в противном случае
     */
    boolean existsByArticle(String article);

    /**
     * Находит товары с ценой выше указанной.
     *
     * @param minPrice минимальная цена для фильтрации товаров
     * @return список товаров, цена которых превышает minPrice
     */
    @Query("SELECT p FROM ProductEntity p WHERE p.price > :minPrice")
    List<ProductEntity> findExpensiveProducts(@Param("minPrice") BigDecimal minPrice);

    /**
     * Находит товары для обновления с пессимистичной блокировкой.
     * <p>
     * Использует PESSIMISTIC_WRITE блокировку для безопасного обновления данных.
     * </p>
     *
     * @param pageable параметры пагинации
     * @return список товаров с установленной блокировкой
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductEntity p ORDER BY p.id")
    List<ProductEntity> findProductsForUpdate(Pageable pageable);

    @Query(value = "SELECT * FROM products ORDER BY id LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<ProductEntity> findProductsForUpdate(@Param("offset") int offset, @Param("limit") int limit);
}