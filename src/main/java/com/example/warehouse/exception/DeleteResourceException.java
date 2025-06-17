package com.example.warehouse.exception;

import java.util.UUID;

/**
 * Исключение, выбрасываемое когда запрашиваемый ресурс не найден по артикулу.
 * <p>
 * Соответствует HTTP статусу 404 (Not Found).
 * </p>
 *
 * @see org.springframework.web.bind.annotation.ResponseStatus
 * @see org.springframework.http.HttpStatus
 */
public class DeleteResourceException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением.
     *
     * @param id детальное сообщение об ошибке
     */
    public DeleteResourceException(String id) {
        super("Product not found with id: " + id);
    }
}
