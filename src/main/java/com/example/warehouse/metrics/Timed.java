package com.example.warehouse.metrics;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для измерения времени выполнения методов.
 * <p>
 * Позволяет задать кастомное имя метрики.
 * Если имя не указано, используется имя метода.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Timed {
    /**
     * Название метрики (по умолчанию - имя метода)
     */
    String value() default "";
}
