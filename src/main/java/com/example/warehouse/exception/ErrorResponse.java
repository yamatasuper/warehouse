package com.example.warehouse.exception;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Стандартизированный ответ об ошибке для REST API.
 * Содержит информацию о времени возникновения, статусе, типе ошибки и детали.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private ZonedDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String exceptionType;
    private List<String> details;
}
