package com.example.warehouse.search.operation;

/**
 * Перечисление поддерживаемых операций поиска.
 * Определяет стандартные операции сравнения и их строковые представления.
 */
public enum SearchOperation {
    /** Операция равенства (=) */
    EQUAL("="),
    /** Операция неравенства (!=) */
    NOT_EQUAL("!="),
    /** Операция "больше или равно" (>=) */
    GREATER_THAN_OR_EQUAL(">="),
    /** Операция "меньше или равно" (<=) */
    LESS_THAN_OR_EQUAL("<="),
    /** Операция поиска по совпадению (~) */
    LIKE("~"),
    /** Операция поиска по содержанию (~) */
    CONTAINS("~");

    private final String operation;

    /**
     * Создает новую операцию поиска.
     *
     * @param operation строковое представление операции
     */
    SearchOperation(String operation) {
        this.operation = operation;
    }

    /**
     * Возвращает строковое представление операции.
     *
     * @return строковое представление операции
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Преобразует строковое представление операции в элемент перечисления.
     *
     * @param text строковое представление операции
     * @return элемент перечисления SearchOperation
     * @throws IllegalArgumentException если переданная строка не соответствует ни одной операции
     */
    public static SearchOperation fromString(String text) {
        for (SearchOperation op : SearchOperation.values()) {
            if (op.operation.equalsIgnoreCase(text)) {
                return op;
            }
            if (op.name().equalsIgnoreCase(text)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Неизвестная операция: " + text);
    }
}