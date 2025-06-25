package com.example.warehouse.search.exception;

/**
 * Исключение, выбрасываемое при некорректных критериях поиска.
 * Может возникать при валидации критериев поиска, когда указаны несуществующие поля,
 * несоответствующие типы значений или недопустимые операции сравнения.
 */
public class InvalidSearchCriteriaException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением об ошибке.
     *
     * @param message детальное сообщение об ошибке
     */
    public InvalidSearchCriteriaException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение с указанным сообщением об ошибке и причиной.
     *
     * @param message детальное сообщение об ошибке
     * @param cause исключение, которое стало причиной данного исключения
     */
    public InvalidSearchCriteriaException(String message, Throwable cause) {
        super(message, cause);
    }
}
