package com.example.warehouse.task_1.schedulers;

import com.example.warehouse.entity.ProductEntity;
import com.example.warehouse.repository.ProductRepository;
import com.example.warehouse.service.ProductServiceMapper;
import com.example.warehouse.task_1.time_metrics.Timed;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Простая реализация сервиса обновления цен продуктов.
 * <p>
 * Основные особенности:
 * - Обновляет цены всех продуктов за один вызов
 * - Использует базовую стратегию увеличения цены на 5%
 * - Логирует процесс обновления
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleProductPriceScheduler implements ProductPriceScheduler {

    private final ProductRepository productRepository;
    private final ProductServiceMapper productServiceMapper;

    @Value("${app.scheduler.price-update.interval-ms:60000}")
    private long intervalMs; // Интервал между запусками в миллисекундах

    @Value("${app.scheduler.price-update.batch-size:1000}")
    private int batchSize; // Размер пакета (не используется в этой реализации)

    /**
     * Метод для обновления цен всех продуктов.
     * <p>
     * Выполняется по расписанию с указанным интервалом.
     * Измеряет время выполнения с помощью аннотации @Timed.
     * Работает в транзакционном контексте (@Transactional).
     * </p>
     */
    @Override
    @Scheduled(fixedRateString = "${app.scheduler.price-update.interval-ms:60000}")
    @Transactional
    @Timed
    public void updateProductPrices() {
        log.debug("Начало обновления цен для всех продуктов");
        List<ProductEntity> products = productRepository.findAll();
        log.debug("Найдено {} продуктов для обновления", products.size());

        products.forEach(product -> {
            BigDecimal newPrice = calculateNewPrice(product.getPrice());
            product.setPrice(newPrice);
        });

        productRepository.saveAll(products);
        log.debug("Обновление цен успешно завершено");
    }

    /**
     * Рассчитывает новую цену (увеличение на 5%).
     *
     * @param currentPrice текущая цена продукта
     * @return новая цена
     */
    private BigDecimal calculateNewPrice(BigDecimal currentPrice) {
        return currentPrice.multiply(BigDecimal.valueOf(1.05));
    }
}