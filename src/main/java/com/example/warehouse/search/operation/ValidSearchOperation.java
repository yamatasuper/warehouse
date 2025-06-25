package com.example.warehouse.search.operation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Аннотация для валидации операций поиска.
 * Гарантирует, что указанная операция поиска является одной из поддерживаемых операций,
 * определенных в перечислении SearchOperation.
 */
@Documented
@Constraint(validatedBy = SearchOperationValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSearchOperation {
    /**
     * Сообщение об ошибке по умолчанию.
     *
     * @return сообщение об ошибке
     */
    String message() default "Invalid search operation";

    /**
     * Группы валидации.
     *
     * @return группы валидации
     */
    Class<?>[] groups() default {};

    /**
     * Полезная нагрузка валидации.
     *
     * @return полезная нагрузка
     */
    Class<? extends Payload>[] payload() default {};
}

