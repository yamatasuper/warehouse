package com.example.warehouse.schedulers;

import com.example.warehouse.metrics.Timed;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Реализация планировщика обновления цен продуктов с оптимизированной пакетной обработкой.
 * Позволяет обновлять цены продуктов с заданным интервалом, используя пакетную обработку
 * для повышения производительности.
 */
@RequiredArgsConstructor
@Slf4j
public class OptimizedProductPriceScheduler implements ProductPriceScheduler {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Директория для сохранения логов изменений цен.
     * Значение по умолчанию: logs/price-updates
     */
    @Value("${app.scheduler.price-update.log-dir:logs/price-updates}")
    private String logDirectory;

    /**
     * Размер пакета для обновления цен.
     * Значение по умолчанию: 10000.
     */
    @Value("${app.scheduler.price-update.batch-size:10000}")
    private int batchSize;

    /**
     * Размер выборки данных из базы.
     * Значение по умолчанию: 1000.
     */
    @Value("${app.scheduler.price-update.fetch-size:1000}")
    private int fetchSize;

    @Override
    @Scheduled(fixedRateString = "${app.scheduler.price-update.interval-ms:60000}")
    @Timed("optimizedPriceUpdate")
    @Transactional
    public void updateProductPrices() {
        long startTime = System.currentTimeMillis();
        AtomicLong totalProcessed = new AtomicLong(0);

        // Создаем директорию для логов, если она не существует
        File logDir = new File(logDirectory);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        // Создаем файл лога с timestamp в имени
        String logFileName = String.format("price-update-%s.log",
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        File logFile = new File(logDir, logFileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile))) {
            jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
                connection.setAutoCommit(false);

                try (PreparedStatement selectStmt = connection.prepareStatement(
                        "SELECT id, article, price FROM products WHERE price IS NOT NULL FOR UPDATE",
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_UPDATABLE)) {

                    selectStmt.setFetchSize(fetchSize);

                    try (ResultSet rs = selectStmt.executeQuery()) {
                        try (PreparedStatement updateStmt = connection.prepareStatement(
                                "UPDATE products SET price = ? WHERE id = ?")) {

                            int batchCount = 0;

                            while (rs.next()) {
                                UUID id = (UUID) rs.getObject("id");
                                String article = rs.getString("article");
                                BigDecimal currentPrice = rs.getBigDecimal("price");
                                BigDecimal newPrice = currentPrice.multiply(BigDecimal.valueOf(1.05));

                                // Логируем изменения в формате: ID,Артикул,СтараяЦена,НоваяЦена
                                writer.write(String.format("%s,%s,%s,%s\n",
                                        id.toString(),
                                        article,
                                        currentPrice.toPlainString(),
                                        newPrice.toPlainString()));

                                updateStmt.setBigDecimal(1, newPrice);
                                updateStmt.setObject(2, id);
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
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                return null;
            });

            long executionTime = System.currentTimeMillis() - startTime;
            log.info("Обновление цен завершено. Обработано {} записей за {} мс ({} записей/сек). Лог изменений сохранен в {}",
                    totalProcessed.get(),
                    executionTime,
                    calculateRecordsPerSecond(totalProcessed.get(), executionTime),
                    logFile.getAbsolutePath());

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