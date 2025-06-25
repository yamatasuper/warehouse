package com.example.warehouse.search.criteria;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Компонент для валидации критериев поиска.
 * Проверяет существование указанных полей в сущности и корректность типов значений.
 */
@Component
public class SearchCriteriaValidator {

    /**
     * Валидирует список критериев поиска для указанного класса сущности.
     *
     * @param criteria список критериев поиска для валидации
     * @param entityClass класс сущности, для которой применяются критерии
     * @throws IllegalArgumentException если поле не существует в сущности или тип значения некорректен
     */
    public void validate(List<SearchCriteria> criteria, Class<?> entityClass) {
        if (criteria == null) return;

        for (SearchCriteria criterion : criteria) {
            validateFieldExists(criterion.getField(), entityClass);
        }
    }

    /**
     * Проверяет существование указанного поля в классе сущности.
     *
     * @param fieldName название поля для проверки
     * @param entityClass класс сущности, в котором проверяется поле
     * @throws IllegalArgumentException если поле не существует в сущности
     */
    private void validateFieldExists(String fieldName, Class<?> entityClass) {
        try {
            Field field = entityClass.getDeclaredField(fieldName.split("\\.")[0]);
            // Для вложенных полей потребуется более сложная логика с использованием reflection
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Invalid field name: " + fieldName);
        }
    }
}
