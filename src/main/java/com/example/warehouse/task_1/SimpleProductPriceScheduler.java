package com.example.warehouse.task_1;

import com.example.warehouse.entity.ProductEntity;
import com.example.warehouse.repository.ProductRepository;
import com.example.warehouse.service.ProductServiceMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleProductPriceScheduler implements ProductPriceScheduler {

    private final ProductRepository productRepository;
    private final ProductServiceMapper productServiceMapper;

    @Value("${app.scheduler.price-update.interval-ms}")
    private long intervalMs;

    @Value("${app.scheduler.price-update.batch-size}")
    private int batchSize;

    @Override
    @Scheduled(fixedRateString = "${app.scheduler.price-update.interval-ms}")
    @Transactional
    @Timed
    public void updateProductPrices() {
        log.debug("Starting price update for all products");
        List<ProductEntity> products = productRepository.findAll();
        log.debug("Found {} products to update", products.size());
        products.stream()
                .forEach(product -> {
                    BigDecimal newPrice = calculateNewPrice(product.getPrice());
                    product.setPrice(newPrice);
                });

        productRepository.saveAll(products);
        log.debug("Price update completed successfully");
    }

    private BigDecimal calculateNewPrice(BigDecimal currentPrice) {
        return currentPrice.multiply(BigDecimal.valueOf(1.05)); // +5%
    }
}