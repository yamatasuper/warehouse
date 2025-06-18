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

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "app.scheduling.enabled", havingValue = "true")
@RequiredArgsConstructor
public class SchedulingConfig {

    private final ProductRepository productRepository;
    private final ProductServiceMapper productServiceMapper;
    private final EntityManager entityManager; // Only needed for optimized scheduler

    @Value("${app.scheduling.optimization:false}")
    private boolean optimizationEnabled;

    @Bean
    @ConditionalOnMissingBean(ProductPriceScheduler.class)
    @ConditionalOnExpression("!${app.scheduling.optimization:false}")
    public ProductPriceScheduler simpleProductPriceScheduler() {
        return new SimpleProductPriceScheduler(productRepository, productServiceMapper);
    }

    @Bean
    @ConditionalOnProperty(name = "app.scheduling.optimization", havingValue = "true")
    public ProductPriceScheduler optimizedProductPriceScheduler() {
        return new OptimizedProductPriceScheduler(productRepository, productServiceMapper, entityManager);
    }
}