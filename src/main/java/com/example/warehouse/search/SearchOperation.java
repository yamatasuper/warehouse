package com.example.warehouse.search;

public enum SearchOperation {
    EQUAL("="),
    NOT_EQUAL("!="),
    GREATER_THAN_OR_EQUAL(">="),
    LESS_THAN_OR_EQUAL("<="),
    LIKE("~"),
    CONTAINS("~");

    private final String operation;

    SearchOperation(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

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