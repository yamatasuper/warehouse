package com.example.warehouse.search;

import com.example.warehouse.persistence.entity.ProductEntity;
import com.example.warehouse.search.criteria.SearchCriteria;
import com.example.warehouse.search.operation.SearchOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jakarta.persistence.criteria.*;

/**
 * Реализация спецификации для фильтрации продуктов на основе критериев поиска.
 * Поддерживает различные типы полей (строки, числа, даты) и операции сравнения.
 */
public class ProductSpecification implements Specification<ProductEntity> {
    private static final Logger logger = LoggerFactory.getLogger(ProductSpecification.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SearchCriteria criteria;

    /**
     * Создает новую спецификацию продукта на основе критерия поиска.
     *
     * @param criteria критерий поиска, содержащий поле, значение и операцию сравнения
     */
    public ProductSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    /**
     * Преобразует критерий поиска в предикат для использования в запросах JPA.
     *
     * @param root корневой объект запроса
     * @param query критерий запроса
     * @param builder построитель критериев
     * @return предикат, соответствующий критерию поиска
     * @throws IllegalArgumentException если критерий некорректен или не может быть преобразован
     */
    @Override
    @SuppressWarnings("unchecked")
    public Predicate toPredicate(Root<ProductEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        try {
            String field = criteria.getField();
            Object value = criteria.getValue();
            SearchOperation operation = SearchOperation.fromString(criteria.getOperation());

            Path<?> path = getPath(root, field);
            Class<?> fieldType = path.getJavaType();

            logger.debug("Building predicate: {} {} {}", field, operation, value);

            // Обработка разных типов полей
            if (String.class.equals(fieldType)) {
                return handleStringPredicate(path, builder, value, operation);
            } else if (Number.class.isAssignableFrom(fieldType)) {
                return handleNumericPredicate(path, builder, value, operation, fieldType);
            } else if (LocalDateTime.class.equals(fieldType)) {
                return handleLocalDateTimePredicate(path, builder, value, operation);
            }

            // Обработка по умолчанию для Comparable типов
            return handleComparablePredicate(path, builder, value, operation);
        } catch (Exception e) {
            logger.error("Error building predicate for criteria: {}", criteria, e);
            throw new IllegalArgumentException("Invalid criteria: " + criteria, e);
        }
    }

    /**
     * Обрабатывает предикаты для строковых полей.
     *
     * @param path путь к полю сущности
     * @param builder построитель критериев
     * @param value значение для сравнения
     * @param operation операция сравнения
     * @return предикат для строкового поля
     * @throws IllegalArgumentException если операция не поддерживается для строк
     */
    private Predicate handleStringPredicate(Path<?> path, CriteriaBuilder builder,
                                            Object value, SearchOperation operation) {
        String strValue = value.toString();
        switch (operation) {
            case EQUAL:
                return builder.equal(path, strValue);
            case LIKE:
            case CONTAINS:
                return builder.like(builder.lower((Expression<String>) path),
                        "%" + strValue.toLowerCase() + "%");
            default:
                throw new IllegalArgumentException("Unsupported string operation: " + operation);
        }
    }

    /**
     * Обрабатывает предикаты для числовых полей.
     *
     * @param path путь к полю сущности
     * @param builder построитель критериев
     * @param value значение для сравнения
     * @param operation операция сравнения
     * @param fieldType тип числового поля
     * @return предикат для числового поля
     * @throws IllegalArgumentException если значение не является числом или операция не поддерживается
     */
    private Predicate handleNumericPredicate(Path<?> path, CriteriaBuilder builder,
                                             Object value, SearchOperation operation,
                                             Class<?> fieldType) {
        // Сначала пробуем использовать значение напрямую, если это Number
        if (value instanceof Number) {
            Number numberValue = (Number) value;
            switch (operation) {
                case EQUAL:
                    return builder.equal(path, numberValue);
                case GREATER_THAN_OR_EQUAL:
                    return builder.ge((Expression<Number>) path, numberValue);
                case LESS_THAN_OR_EQUAL:
                    return builder.le((Expression<Number>) path, numberValue);
                default:
                    throw new IllegalArgumentException("Unsupported numeric operation: " + operation);
            }
        }

        // Если не Number, пробуем распарсить
        try {
            String strValue = value.toString();
            if (Double.class.equals(fieldType) || double.class.equals(fieldType)) {
                Double doubleValue = Double.parseDouble(strValue);
                switch (operation) {
                    case EQUAL:
                        return builder.equal(path, doubleValue);
                    case GREATER_THAN_OR_EQUAL:
                        return builder.ge((Expression<Number>) path, doubleValue);
                    case LESS_THAN_OR_EQUAL:
                        return builder.le((Expression<Number>) path, doubleValue);
                }
            } else if (Float.class.equals(fieldType) || float.class.equals(fieldType)) {
                Float floatValue = Float.parseFloat(strValue);
                switch (operation) {
                    case EQUAL:
                        return builder.equal(path, floatValue);
                    case GREATER_THAN_OR_EQUAL:
                        return builder.ge((Expression<Number>) path, floatValue);
                    case LESS_THAN_OR_EQUAL:
                        return builder.le((Expression<Number>) path, floatValue);
                }
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric value: " + value + " for field type " + fieldType, e);
        }

        throw new IllegalArgumentException("Unsupported numeric field type: " + fieldType);
    }

    /**
     * Обрабатывает предикаты для полей типа LocalDateTime.
     *
     * @param path путь к полю сущности
     * @param builder построитель критериев
     * @param value значение даты/времени
     * @param operation операция сравнения
     * @return предикат для поля LocalDateTime
     * @throws IllegalArgumentException если формат даты некорректен или операция не поддерживается
     */
    private Predicate handleLocalDateTimePredicate(Path<?> path, CriteriaBuilder builder,
                                                   Object value, SearchOperation operation) {
        try {
            LocalDateTime dateValue;
            if (value instanceof LocalDateTime) {
                dateValue = (LocalDateTime) value;
            } else {
                dateValue = LocalDateTime.parse(value.toString(), DATE_FORMATTER);
            }

            switch (operation) {
                case EQUAL:
                    return builder.equal(path, dateValue);
                case GREATER_THAN_OR_EQUAL:
                    return builder.greaterThanOrEqualTo((Expression<LocalDateTime>) path, dateValue);
                case LESS_THAN_OR_EQUAL:
                    return builder.lessThanOrEqualTo((Expression<LocalDateTime>) path, dateValue);
                default:
                    throw new IllegalArgumentException("Unsupported date operation: " + operation);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected: yyyy-MM-dd HH:mm:ss", e);
        }
    }

    /**
     * Обрабатывает предикаты для Comparable типов (общая реализация).
     *
     * @param path путь к полю сущности
     * @param builder построитель критериев
     * @param value значение для сравнения
     * @param operation операция сравнения
     * @return предикат для Comparable типа
     * @throws IllegalArgumentException если значение не Comparable или операция не поддерживается
     */
    @SuppressWarnings("unchecked")
    private Predicate handleComparablePredicate(Path<?> path, CriteriaBuilder builder,
                                                Object value, SearchOperation operation) {
        if (!(value instanceof Comparable)) {
            throw new IllegalArgumentException("Value must be Comparable for field: " + path.getJavaType());
        }

        Comparable comparableValue = (Comparable) value;
        switch (operation) {
            case EQUAL:
                return builder.equal(path, value);
            case GREATER_THAN_OR_EQUAL:
                return builder.greaterThanOrEqualTo((Expression<Comparable>) path, comparableValue);
            case LESS_THAN_OR_EQUAL:
                return builder.lessThanOrEqualTo((Expression<Comparable>) path, comparableValue);
            default:
                throw new IllegalArgumentException("Unsupported operation for comparable type: " + operation);
        }
    }

    /**
     * Получает путь к полю сущности, включая вложенные свойства.
     *
     * @param root корневой объект сущности
     * @param field название поля (может содержать точки для вложенных свойств)
     * @return путь к полю
     */
    private Path<?> getPath(Root<ProductEntity> root, String field) {
        if (field.contains(".")) {
            String[] parts = field.split("\\.");
            Path<?> path = root.get(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                path = path.get(parts[i]);
            }
            return path;
        }
        return root.get(field);
    }
}