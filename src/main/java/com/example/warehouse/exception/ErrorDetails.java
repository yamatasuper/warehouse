package com.example.warehouse.exception;

import lombok.Builder;
import lombok.Data;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Data
@Builder
public class ErrorDetails {
    private String exceptionName;
    private String className;
    private String message;
    private ZonedDateTime timestamp;

    public static ErrorDetails fromException(Exception ex) {
        return ErrorDetails.builder()
                .exceptionName(ex.getClass().getSimpleName())
                .className(ex.getClass().getName())
                .message(ex.getMessage())
                .timestamp(ZonedDateTime.now(ZoneOffset.UTC))
                .build();
    }
}
