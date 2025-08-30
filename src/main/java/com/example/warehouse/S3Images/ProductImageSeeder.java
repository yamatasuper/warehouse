package com.example.warehouse.S3Images;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.example.warehouse.persistence.entity.ProductEntity;
import com.example.warehouse.persistence.repository.ProductRepository;
import com.example.warehouse.service.ProductService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;

@Service
public class ProductImageSeeder {

  public ProductImageSeeder(
          ProductService productService,
          S3Service s3Service,
          ProductImageRepository productImageRepository,
          ResourcePatternResolver resourcePatternResolver, ProductRepository productRepository) {
    this.productService = productService;
    this.s3Service = s3Service;
    this.productImageRepository = productImageRepository;
    this.resourcePatternResolver = resourcePatternResolver;
    this.productRepository = productRepository;
  }

  private final ProductService productService;
  private final S3Service s3Service;
  private final ProductImageRepository productImageRepository;
  private final ResourcePatternResolver resourcePatternResolver;
  private final ProductRepository productRepository;

  public void seedRandomImagesToProducts(int imagesPerProduct) throws IOException {
    Resource[] images = resourcePatternResolver.getResources("classpath:seed-images/*");
    if (images.length == 0) {
      throw new IllegalStateException("No seed images found in resources/seed-images");
    }

    // Use repository instead of service to get entities
    List<ProductEntity> products = productRepository.findAll(); // Changed to use repository
    Random random = new Random();

    for (ProductEntity product : products) { // Changed to ProductEntity
      for (int i = 0; i < imagesPerProduct; i++) {
        Resource image = images[random.nextInt(images.length)];
        String filename = image.getFilename();
        String extension = filename.substring(filename.lastIndexOf("."));
        String s3Key = UUID.randomUUID() + extension;

        // Upload to MinIO
        try (InputStream is = image.getInputStream()) {
          byte[] bytes = is.readAllBytes();
          s3Service.uploadFile(
                  new MockMultipartFile(
                          filename,
                          filename,
                          "image/jpeg", // или "image/png" в зависимости от типа
                          bytes),
                  s3Key);
        }

        // Save to DB
        ProductImage imageEntity = new ProductImage();
        imageEntity.setProduct(product); // This should work if ProductEntity is compatible
        imageEntity.setS3Key(s3Key);
        productImageRepository.save(imageEntity);
      }
    }
  }
}
