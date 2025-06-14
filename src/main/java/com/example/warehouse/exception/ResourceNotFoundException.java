package com.example.warehouse.exception;

/**
 * Исключение, выбрасываемое когда запрашиваемый ресурс не найден.
 * <p>
 * Соответствует HTTP статусу 404 (Not Found).
 * </p>
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
