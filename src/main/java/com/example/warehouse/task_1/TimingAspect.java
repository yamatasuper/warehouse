package com.example.warehouse.task_1;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class TimingAspect {

    private final MeterRegistry meterRegistry;

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
