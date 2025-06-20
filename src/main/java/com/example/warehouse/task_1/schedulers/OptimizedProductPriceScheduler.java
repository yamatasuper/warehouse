package com.example.warehouse.task_1.schedulers;

import com.example.warehouse.entity.ProductEntity;
import com.example.warehouse.repository.ProductRepository;
import com.example.warehouse.service.ProductServiceMapper;
import com.example.warehouse.task_1.time_metrics.Timed;

import org.springframework.beans.factory.annotation.Value;
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
    private final ProductServiceMapper productServiceMapper;
    private final EntityManager entityManager;

    /**
     * Размер пакета для обработки продуктов
     */
    @Value("${app.scheduler.price-update.batch-size: 1000}")
    private int batchSize;

    /**
     * Метод для обновления цен продуктов по расписанию.
     * <p>
     * Алгоритм работы:
     * 1. Создает временный CSV файл для логов
     * 2. Обрабатывает продукты пакетами
     * 3. Для каждого продукта рассчитывает новую цену (+5%)
     * 4. Сохраняет изменения и логирует в CSV
     * 5. Очищает кэш EntityManager между пакетами
     * </p>
     */
    @Override
    @Scheduled(fixedRateString = "${app.scheduler.price-update.interval-ms:60000}")
    @Timed("optimizedPriceUpdate")
    @Transactional
    public void updateProductPrices() {
        log.info("Запуск обновления цен с размером пакета: {}", batchSize);

        try {
            // Создаем временный файл для логов изменений
            Path tempFile = Files.createTempFile("product_prices_", ".csv");
            log.debug("Создан временный файл для логов: {}", tempFile.toAbsolutePath());

            try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
                log.debug("Начало пакетной обработки...");
                processProductsInBatches(writer);
                log.info("Обработка завершена. Логи сохранены в: {}", tempFile);
            }

        } catch (IOException e) {
            log.error("Ошибка при обновлении цен", e);
            throw new RuntimeException("Ошибка при обновлении цен", e);
        }
    }

    /**
     * Обрабатывает продукты пакетами.
     *
     * @param writer writer для записи логов в CSV
     * @throws IOException при ошибках записи в файл
     */
    private void processProductsInBatches(BufferedWriter writer) throws IOException {
        int pageNumber = 0;
        int totalProcessed = 0;

        while (true) {
            Pageable pageable = PageRequest.of(pageNumber, batchSize);
            log.debug("Получение пакета {} размером {}", pageNumber, batchSize);

            // Получаем пакет продуктов с блокировкой для обновления
            List<ProductEntity> batch = productRepository.findProductsForUpdate(pageable);
            if (batch.isEmpty()) {
                log.debug("Продукты для обработки закончились");
                break;
            }

            log.debug("Обработка пакета из {} продуктов", batch.size());

            for (ProductEntity product : batch) {
                BigDecimal oldPrice = product.getPrice();
                BigDecimal newPrice = calculateNewPrice(oldPrice);
                product.setPrice(newPrice);

                // Логируем изменения в формате: ID,Артикул,СтараяЦена,НоваяЦена
                writer.write(String.format("%s,%s,%s,%s\n",
                        product.getId(),
                        product.getArticle(),
                        oldPrice,
                        newPrice));
            }

            productRepository.saveAll(batch);
            entityManager.clear(); // Очищаем кэш между пакетами
            totalProcessed += batch.size();
            log.debug("Пакет {} обработан. Всего: {}", pageNumber, totalProcessed);
            pageNumber++;
        }

        log.info("Обработка завершена. Всего обновлено продуктов: {}", totalProcessed);
    }

    /**
     * Рассчитывает новую цену (увеличение на 5%).
     *
     * @param currentPrice текущая цена продукта
     * @return новая цена
     */
    private BigDecimal calculateNewPrice(BigDecimal currentPrice) {
        BigDecimal newPrice = currentPrice.multiply(BigDecimal.valueOf(1.05));
        log.trace("Обновление цены: {} -> {}", currentPrice, newPrice);
        return newPrice;
    }
}