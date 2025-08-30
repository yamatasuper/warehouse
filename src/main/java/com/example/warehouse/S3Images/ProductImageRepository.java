package com.example.warehouse.S3Images;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {
  List<ProductImage> findByProductId(UUID productId);
}
