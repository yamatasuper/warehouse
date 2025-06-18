package com.example.warehouse.task_1;

import com.example.warehouse.entity.ProductEntity;
import com.example.warehouse.repository.ProductRepository;
import com.example.warehouse.service.ProductServiceMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OptimizedProductPriceScheduler implements ProductPriceScheduler {

    private final ProductRepository productRepository;
    private final ProductServiceMapper productServiceMapper;
    private final EntityManager entityManager;

    @Value("${app.scheduler.price-update.batch-size}")
    private int batchSize;

    @Override
    @Scheduled(fixedRateString = "${app.scheduler.price-update.interval-ms}")
    @Timed("optimizedPriceUpdate") // Наша кастомная аннотация
    @Transactional
    public void updateProductPrices() {
        log.info("Starting optimized price update with batch size: {}", batchSize);

        try {
            // Создаем временный CSV файл для отладки/логирования изменений
            Path tempFile = Files.createTempFile("product_prices_", ".csv");
            log.debug("Created temp file for price updates: {}", tempFile.toAbsolutePath());

            try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
                log.debug("Processing products in batches...");
                processProductsInBatches(writer);
                log.info("Successfully processed all batches. Updated prices saved to: {}", tempFile);
            }

            // Для просмотра файла:
            // 1. Лог показывает полный путь (например /tmp/product_prices_12345.csv)
            // 2. Можно открыть в любом редакторе или командой:
            //    cat /tmp/product_prices_12345.csv
            // 3. Файл автоматически удалится при завершении работы JVM

        } catch (IOException e) {
            log.error("Failed to update prices", e);
            throw new RuntimeException("Failed to update prices", e);
        }
    }

    private void processProductsInBatches(BufferedWriter writer) throws IOException {
        int pageNumber = 0;
        int totalProcessed = 0;

        while (true) {
            Pageable pageable = PageRequest.of(pageNumber, batchSize);
            log.debug("Fetching batch {} with size {}", pageNumber, batchSize);

            List<ProductEntity> batch = productRepository.findProductsForUpdate(pageable);
            if (batch.isEmpty()) {
                log.debug("No more products to process");
                break;
            }

            log.debug("Processing batch with {} products", batch.size());

            for (ProductEntity product : batch) {
                BigDecimal oldPrice = product.getPrice();
                BigDecimal newPrice = calculateNewPrice(oldPrice);
                product.setPrice(newPrice);

                // Записываем изменения в CSV: ID,Артикул,СтараяЦена,НоваяЦена
                writer.write(String.format("%s,%s,%s,%s\n",
                        product.getId(),
                        product.getArticle(),
                        oldPrice,
                        newPrice));
            }

            productRepository.saveAll(batch);
            entityManager.clear();
            totalProcessed += batch.size();
            log.debug("Batch {} processed. Total processed: {}", pageNumber, totalProcessed);
            pageNumber++;
        }

        log.info("Finished processing. Total products updated: {}", totalProcessed);
    }

    private BigDecimal calculateNewPrice(BigDecimal currentPrice) {
        BigDecimal newPrice = currentPrice.multiply(BigDecimal.valueOf(1.05));
        log.trace("Price update: {} -> {}", currentPrice, newPrice);
        return newPrice;
    }
}