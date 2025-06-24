package com.example.warehouse.search;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

@Component
public class SearchCriteriaValidator {

    public void validate(List<SearchCriteria> criteria, Class<?> entityClass) {
        if (criteria == null) return;

        for (SearchCriteria criterion : criteria) {
            validateFieldExists(criterion.getField(), entityClass);
            validateValueType(criterion);
        }
    }

    private void validateFieldExists(String fieldName, Class<?> entityClass) {
        try {
            Field field = entityClass.getDeclaredField(fieldName.split("\\.")[0]);
            // For nested fields, you'd need more complex reflection
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Invalid field name: " + fieldName);
        }
    }

    private void validateValueType(SearchCriteria criterion) {
        // Implement type checking based on your entity fields
    }
}
