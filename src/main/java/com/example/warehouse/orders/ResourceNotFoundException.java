package com.example.warehouse.orders;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(UUID id) {
        super("Resource not found with id: " + id);
    }

    public ResourceNotFoundException(Long id) {
        super("Resource not found with id: " + id);
    }
}
