package com.example.warehouse.search.operation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Валидатор для проверки корректности операций поиска.
 * Реализует логику проверки, что переданная операция является одной из допустимых,
 * определенных в перечислении SearchOperation.
 */
public class SearchOperationValidator implements ConstraintValidator<ValidSearchOperation, String> {
    /**
     * Проверяет валидность операции поиска.
     *
     * @param value значение для валидации
     * @param context контекст валидатора
     * @return true если операция валидна, false в противном случае
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        try {
            SearchOperation.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}