package com.example.warehouse.S3Images;


import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.warehouse.persistence.entity.ProductEntity;
import com.example.warehouse.persistence.repository.ProductRepository;
import com.example.warehouse.service.model.Product;
import org.apache.commons.io.FilenameUtils;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products/{productId}/images")
public class ProductImageController {
  private final ProductImageRepository productImageRepository;
  private final ProductRepository productRepository;
  private final S3Service s3Service;

  // Constructor injection
  public ProductImageController(
      ProductImageRepository productImageRepository,
      ProductRepository productRepository,
      S3Service s3Service) {
    this.productImageRepository = productImageRepository;
    this.productRepository = productRepository;
    this.s3Service = s3Service;
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ProductImageDto> uploadImage(
      @PathVariable UUID productId, @RequestParam("file") MultipartFile file) throws IOException {

    ProductEntity product = // Предполагается, что это ProductEntity
            productRepository
                    .findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
    String s3Key = UUID.randomUUID() + "." + fileExtension;

    s3Service.uploadFile(file, s3Key);

    ProductImage image = new ProductImage();
    image.setProduct(product);
    image.setS3Key(s3Key);
    productImageRepository.save(image);

    return ResponseEntity.ok(mapToDto(image));
  }

  @GetMapping
  public ResponseEntity<List<ProductImageDto>> getProductImages(@PathVariable UUID productId) {
    List<ProductImage> images = productImageRepository.findByProductId(productId);
    return ResponseEntity.ok(images.stream().map(this::mapToDto).collect(Collectors.toList()));
  }

  private ProductImageDto mapToDto(ProductImage image) {
    ProductImageDto dto = new ProductImageDto();
    dto.setId(image.getId());
    dto.setProductId(image.getProduct().getId());
    dto.setUrl(s3Service.getFileUrl(image.getS3Key()));
    dto.setCreatedAt(image.getCreatedAt());
    return dto;
  }
}
