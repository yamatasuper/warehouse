package com.example.warehouse.repository;

import com.example.warehouse.entity.ProductEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    // Кастомные методы
    boolean existsByArticle(String article);

    @Query("SELECT p FROM ProductEntity p WHERE p.price > :minPrice")
    List<ProductEntity> findExpensiveProducts(@Param("minPrice") BigDecimal minPrice);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductEntity p ORDER BY p.id")
    List<ProductEntity> findProductsForUpdate(Pageable pageable);
}
