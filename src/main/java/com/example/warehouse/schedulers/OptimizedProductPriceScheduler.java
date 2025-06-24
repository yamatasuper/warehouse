package com.example.warehouse.schedulers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.*;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class OptimizedProductPriceScheduler implements ProductPriceScheduler {

    private final JdbcTemplate jdbcTemplate;

    @Value("${app.scheduler.price-update.batch-size:10000}")
    private int batchSize;

    @Value("${app.scheduler.price-update.fetch-size:1000}")
    private int fetchSize;

    @Override
    @Scheduled(fixedRateString = "${app.scheduler.price-update.interval-ms:60000}")
    @Transactional
    public void updateProductPrices() {
        long startTime = System.currentTimeMillis();
        AtomicLong totalProcessed = new AtomicLong(0);

        try {
            jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
                connection.setAutoCommit(false);

                try (PreparedStatement selectStmt = connection.prepareStatement(
                        "SELECT id, price FROM products WHERE price IS NOT NULL FOR UPDATE",
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_UPDATABLE)) {

                    selectStmt.setFetchSize(fetchSize);

                    try (ResultSet rs = selectStmt.executeQuery()) {
                        try (PreparedStatement updateStmt = connection.prepareStatement(
                                "UPDATE products SET price = ? WHERE id = ?")) {

                            int batchCount = 0;

                            while (rs.next()) {
                                UUID id = (UUID) rs.getObject("id"); // Получаем ID как UUID
                                BigDecimal currentPrice = rs.getBigDecimal("price");
                                BigDecimal newPrice = currentPrice.multiply(BigDecimal.valueOf(1.05));

                                updateStmt.setBigDecimal(1, newPrice);
                                updateStmt.setObject(2, id); // Устанавливаем UUID параметр
                                updateStmt.addBatch();

                                if (++batchCount % batchSize == 0) {
                                    updateStmt.executeBatch();
                                    totalProcessed.addAndGet(batchCount);
                                    batchCount = 0;
                                    log.debug("Обработано {} записей", totalProcessed.get());
                                }
                            }

                            if (batchCount > 0) {
                                updateStmt.executeBatch();
                                totalProcessed.addAndGet(batchCount);
                            }
                        }
                    }
                }
                return null;
            });

            long executionTime = System.currentTimeMillis() - startTime;
            log.info("Обновление цен завершено. Обработано {} записей за {} мс ({} записей/сек)",
                    totalProcessed.get(),
                    executionTime,
                    calculateRecordsPerSecond(totalProcessed.get(), executionTime));

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Ошибка при обновлении цен. Время выполнения: {} мс", executionTime, e);
            throw new RuntimeException("Ошибка при обновлении цен", e);
        }
    }

    private long calculateRecordsPerSecond(long totalCount, long executionTimeMs) {
        return executionTimeMs > 0 ? (totalCount * 1000) / executionTimeMs : 0;
    }
}