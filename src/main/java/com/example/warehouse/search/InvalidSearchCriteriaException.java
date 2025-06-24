package com.example.warehouse.search;

public class InvalidSearchCriteriaException extends RuntimeException {
    public InvalidSearchCriteriaException(String message) {
        super(message);
    }

    public InvalidSearchCriteriaException(String message, Throwable cause) {
        super(message, cause);
    }
}

