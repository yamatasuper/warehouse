package com.example.warehouse.exception;

/**
 * Исключение, выбрасываемое при попытке создать или обновить ресурс, который уже существует.
 */
public class DuplicateResourceException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением.
     *
     * @param message сообщение, описывающее причину исключения
     */
    public DuplicateResourceException(String message) {
        super(message);
    }
}