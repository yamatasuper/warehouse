package com.example.warehouse.exception;

/**
 * Исключение, выбрасываемое при невалидных параметрах запроса.
 *
 * @param message детальное сообщение об ошибке
 */
public class InvalidParameterException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением.
     *
     * @param message детальное сообщение об ошибке
     */
    public InvalidParameterException(String message) {
        super(message);
    }
}