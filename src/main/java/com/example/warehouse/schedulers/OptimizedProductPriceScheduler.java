package com.example.warehouse.schedulers;

import com.example.warehouse.entity.ProductEntity;
import com.example.warehouse.repository.ProductRepository;
import com.example.warehouse.service.ProductServiceMapper;
import com.example.warehouse.time_metrics.Timed;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;

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

/**
 * Сервис для оптимизированного обновления цен продуктов по расписанию.
 * <p>
 * Основные особенности:
 * - Пакетная обработка для снижения нагрузки на БД
 * - Поддержка транзакций
 * - Логирование изменений в CSV файл
 * - Автоматическое масштабирование цены на 5%
 * </p>
 */
@RequiredArgsConstructor
@Slf4j
public class OptimizedProductPriceScheduler implements ProductPriceScheduler {

    private final ProductRepository productRepository;
    private final EntityManager entityManager;
    private final JdbcTemplate jdbcTemplate;

    @Value("${app.scheduler.price-update.batch-size:10000}")
    private int batchSize;

    @Override
    @Scheduled(fixedRateString = "${app.scheduler.price-update.interval-ms:60000}")
    @Timed("optimizedPriceUpdate")
    @Transactional
    public void updateProductPrices() {
        long startTime = System.currentTimeMillis();
        long totalCount = productRepository.count();

        log.info("Начало обновления цен. Всего записей: {}", totalCount);

        try {
                log.info("Используется пакетное обновление (batch)");
                batchUpdatePrices();

            long executionTime = System.currentTimeMillis() - startTime;
            log.info("Обновление цен завершено. Обработано {} записей за {} мс ({} записей/сек)",
                    totalCount,
                    executionTime,
                    calculateRecordsPerSecond(totalCount, executionTime));
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Ошибка при обновлении цен. Время выполнения: {} мс", executionTime, e);
            throw new RuntimeException("Ошибка при обновлении цен", e);
        }
    }

    private void batchUpdatePrices() {
        int offset = 0;
        int totalProcessed = 0;

        while (true) {
            long batchStart = System.currentTimeMillis();

            List<ProductEntity> batch = productRepository.findProductsForUpdate(offset, batchSize);
            if (batch.isEmpty()) {
                break;
            }

            batch.forEach(product ->
                    product.setPrice(calculateNewPrice(product.getPrice()))
            );

            productRepository.saveAll(batch);
            entityManager.flush();
            entityManager.clear();

            totalProcessed += batch.size();
            offset += batchSize;

            log.debug("Обработан пакет {}-{} ({} записей) за {} мс",
                    offset - batchSize,
                    offset - 1,
                    batch.size(),
                    System.currentTimeMillis() - batchStart);
        }
    }

    private void bulkUpdatePrices() {
        long startTime = System.currentTimeMillis();

        String updateQuery = "UPDATE products SET price = price * 1.05 WHERE price IS NOT NULL";

        int updatedCount = jdbcTemplate.update(updateQuery);

        log.info("Bulk-обновление завершено. Обновлено {} записей за {} мс",
                updatedCount,
                System.currentTimeMillis() - startTime);
    }

    private BigDecimal calculateNewPrice(BigDecimal currentPrice) {
        return currentPrice.multiply(BigDecimal.valueOf(1.05));
    }

    private long calculateRecordsPerSecond(long totalCount, long executionTimeMs) {
        return executionTimeMs > 0 ? (totalCount * 1000) / executionTimeMs : 0;
    }
}