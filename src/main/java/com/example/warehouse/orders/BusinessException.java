package com.example.warehouse.orders;

import java.util.UUID;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

