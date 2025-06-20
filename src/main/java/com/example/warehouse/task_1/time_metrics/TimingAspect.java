package com.example.warehouse.task_1.time_metrics;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Аспект для измерения времени выполнения методов.
 * <p>
 * Обеспечивает:
 * - Замер времени методов с аннотацией @Timed
 * - Замер времени транзакционных методов
 * - Экспорт метрик через MeterRegistry
 * </p>
 */
@Aspect
@Component
@RequiredArgsConstructor
public class TimingAspect {

    private final MeterRegistry meterRegistry; // Реестр метрик Micrometer

    /**
     * Замеряет время выполнения методов с аннотацией @Timed.
     *
     * @param joinPoint точка соединения
     * @param timed аннотация метода
     * @return результат выполнения метода
     */
    @Around("@annotation(timed)")
    public Object measureTime(ProceedingJoinPoint joinPoint, Timed timed) throws Throwable {
        String metricName = timed.value().isEmpty() ?
                joinPoint.getSignature().getName() : timed.value();

        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            return joinPoint.proceed();
        } finally {
            sample.stop(Timer.builder(metricName)
                    .tags("class", joinPoint.getSignature().getDeclaringType().getSimpleName())
                    .register(meterRegistry));
        }
    }

    /**
     * Замеряет время выполнения транзакционных методов.
     *
     * @param joinPoint точка соединения
     * @param transactional аннотация @Transactional
     * @return результат выполнения метода
     */
    @Around("@annotation(transactional)")
    public Object measureTransactionTime(ProceedingJoinPoint joinPoint, Transactional transactional) throws Throwable {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            return joinPoint.proceed();
        } finally {
            sample.stop(Timer.builder("transaction.time")
                    .tags("class", joinPoint.getSignature().getDeclaringType().getSimpleName())
                    .register(meterRegistry));
        }
    }
}