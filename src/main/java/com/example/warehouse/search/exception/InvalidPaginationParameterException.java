package com.example.warehouse.search.exception;

/**
 * Исключение, выбрасываемое при некорректных параметрах пагинации.
 * Например, когда номер страницы или размер страницы имеют недопустимые значения.
 */
public class InvalidPaginationParameterException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением об ошибке.
     *
     * @param message детальное сообщение об ошибке
     */
    public InvalidPaginationParameterException(String message) {
        super(message);
    }
}
