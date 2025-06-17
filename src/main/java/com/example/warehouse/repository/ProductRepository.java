package com.example.warehouse.repository;

import com.example.warehouse.entity.ProductEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Репозиторий для доступа к данным товаров.
 * <p>
 * Наследует стандартные CRUD операции от JpaRepository.
 * </p>
 *
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    boolean existsByArticle(String article);
}
