package com.example.warehouse.S3Images;

import com.example.warehouse.enums.ProductCategoryEnum;
import com.example.warehouse.persistence.entity.ProductEntity;
import com.example.warehouse.service.model.Product;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {
  private UUID id;
  private String name;
  private String article; // Изменено с sku на article
  private String description;
  private ProductCategoryEnum category; // Изменен тип на enum
  private BigDecimal price;
  private BigDecimal quantity; // Изменен тип на BigDecimal
  private ZonedDateTime lastQuantityChange; // Добавлено новое поле
  private ZonedDateTime createdAt; // Изменен тип на LocalDate
  private List<String> imageUrls;
  private Boolean isAvailable;

  public static ProductDto fromProduct(ProductEntity product, List<String> imageUrls) {
    ProductDto dto = new ProductDto();
    dto.setId(product.getId());
    dto.setName(product.getName());
    dto.setArticle(product.getArticle());
    dto.setDescription(product.getArticle());
    dto.setCategory(product.getCategory());
    dto.setPrice(product.getPrice());
    dto.setQuantity(product.getQuantity());
    dto.setLastQuantityChange(product.getLastQuantityChange());
    dto.setCreatedAt(product.getCreatedAt());
    dto.setImageUrls(imageUrls);
    dto.setIsAvailable(product.getIsAvailable());
    return dto;
  }
}