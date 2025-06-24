package com.example.warehouse.search;

import static org.hibernate.type.descriptor.java.JdbcDateJavaType.DATE_FORMAT;

import com.example.warehouse.entity.ProductEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import jakarta.persistence.criteria.*;

public class ProductSpecification implements Specification<ProductEntity> {
    private static final Logger logger = LoggerFactory.getLogger(ProductSpecification.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SearchCriteria criteria;

    public ProductSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

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

            // Handle different field types
            if (String.class.equals(fieldType)) {
                return handleStringPredicate(path, builder, value, operation);
            } else if (Number.class.isAssignableFrom(fieldType)) {
                return handleNumericPredicate(path, builder, value, operation, fieldType);
            } else if (LocalDateTime.class.equals(fieldType)) {
                return handleLocalDateTimePredicate(path, builder, value, operation);
            }

            // Default handling for Comparable types
            return handleComparablePredicate(path, builder, value, operation);
        } catch (Exception e) {
            logger.error("Error building predicate for criteria: {}", criteria, e);
            throw new IllegalArgumentException("Invalid criteria: " + criteria, e);
        }
    }

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

    private Predicate handleNumericPredicate(Path<?> path, CriteriaBuilder builder,
                                             Object value, SearchOperation operation,
                                             Class<?> fieldType) {
        // First try to use the value directly if it's already a Number
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

        // If not a Number, try to parse it
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

    private Predicate handleDatePredicate(Path<?> path, CriteriaBuilder builder,
                                          Object value, SearchOperation operation) {
        try {
            Date dateValue;
            if (value instanceof Date) {
                dateValue = (Date) value;
            } else {
                SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
                dateValue = format.parse(value.toString());
            }

            switch (operation) {
                case EQUAL:
                    return builder.equal(path, dateValue);
                case GREATER_THAN_OR_EQUAL:
                    return builder.greaterThanOrEqualTo((Expression<Date>) path, dateValue);
                case LESS_THAN_OR_EQUAL:
                    return builder.lessThanOrEqualTo((Expression<Date>) path, dateValue);
                default:
                    throw new IllegalArgumentException("Unsupported date operation: " + operation);
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected: " + DATE_FORMAT, e);
        }
    }

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