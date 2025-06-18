package com.example.warehouse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * Исключение, выбрасываемое когда запрашиваемый ресурс не найден.
 * <p>
 * Соответствует HTTP статусу 404 (Not Found).
 * </p>
 *
 * @see org.springframework.web.bind.annotation.ResponseStatus
 * @see org.springframework.http.HttpStatus
 */

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением.
     *
     * @param id детальное сообщение об ошибке
     */
    public ResourceNotFoundException(UUID id) {
        super("Product not found with id: " + id);
    }
}
