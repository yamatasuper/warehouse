package com.example.warehouse.search;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class SearchOperationValidator implements ConstraintValidator<ValidSearchOperation, String> {
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