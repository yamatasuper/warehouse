package com.example.warehouse.task_1;

import com.example.warehouse.repository.ProductRepository;
import com.example.warehouse.service.ProductServiceMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

/**
 * Конфигурационный класс для настройки планировщика обновления цен.
 * <p>
 * Активирует Scheduling и предоставляет реализации ProductPriceScheduler
 * в зависимости от настроек приложения.
 * </p>
 */
@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "app.scheduling.enabled", havingValue = "true")
@RequiredArgsConstructor
public class SchedulingConfig {

    private final ProductRepository productRepository;
    private final ProductServiceMapper productServiceMapper;
    private final EntityManager entityManager; // Требуется только для оптимизированной версии

    /**
     * Флаг активации оптимизированной версии планировщика.
     * По умолчанию: false.
     */
    @Value("${app.scheduling.optimization:false}")
    private boolean optimizationEnabled;

    /**
     * Создает простую версию планировщика цен.
     * <p>
     * Активируется когда:
     * - Оптимизация отключена (optimizationEnabled=false)
     * - Нет других бинов ProductPriceScheduler
     * </p>
     *
     * @return экземпляр SimpleProductPriceScheduler
     */
    @Bean
    @ConditionalOnMissingBean(ProductPriceScheduler.class)
    @ConditionalOnExpression("!${app.scheduling.optimization:false}")
    public ProductPriceScheduler simpleProductPriceScheduler() {
        return new SimpleProductPriceScheduler(productRepository, productServiceMapper);
    }

    /**
     * Создает оптимизированную версию планировщика цен.
     * <p>
     * Активируется когда:
     * - Включена оптимизация (app.scheduling.optimization=true)
     * - Использует EntityManager для пакетной обработки
     * </p>
     *
     * @return экземпляр OptimizedProductPriceScheduler
     */
    @Bean
    @ConditionalOnProperty(name = "app.scheduling.optimization", havingValue = "true")
    public ProductPriceScheduler optimizedProductPriceScheduler() {
        return new OptimizedProductPriceScheduler(productRepository, productServiceMapper, entityManager);
    }
}