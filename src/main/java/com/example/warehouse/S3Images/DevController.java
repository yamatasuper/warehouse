package com.example.warehouse.S3Images;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dev")
public class DevController {

  private final ProductImageSeeder productImageSeeder;

  // Use constructor injection
  public DevController(ProductImageSeeder productImageSeeder) {
    this.productImageSeeder = productImageSeeder;
  }

  @PostMapping("/seed-images")
  public ResponseEntity<String> seedImages(@RequestParam int imagesPerProduct) {
    try {
      productImageSeeder.seedRandomImagesToProducts(imagesPerProduct);
      return ResponseEntity.ok("Images seeded successfully");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error seeding images: " + e.getMessage());
    }
  }
}
