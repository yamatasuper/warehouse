package com.example.warehouse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.security.InvalidParameterException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Глобальный обработчик исключений для REST API.
 * Обрабатывает различные типы исключений и возвращает стандартизированные ответы.
 */
@RestControllerAdvice
@Tag(name = "Обработка ошибок", description = "Глобальная обработка исключений API")
public class GlobalExceptionHandler {

    /**
     * Обрабатывает все неперехваченные исключения.
     *
     * @param ex исключение
     * @return ResponseEntity с ErrorResponse
     */
    @Operation(summary = "Обработка внутренних ошибок сервера")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception ex) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(ZonedDateTime.now(ZoneOffset.UTC))
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Произошла непредвиденная ошибка")
                .exceptionType(ex.getClass().getSimpleName())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Обрабатывает исключения валидации.
     *
     * @param ex исключение MethodArgumentNotValidException
     * @return ResponseEntity с ErrorResponse
     */
    @Operation(summary = "Обработка ошибок валидации")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    })
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(ZonedDateTime.now(ZoneOffset.UTC))
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .details(errors)
                .exceptionType(ex.getClass().getSimpleName())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Обрабатывает исключение DuplicateResourceException.
     *
     * @param ex исключение DuplicateResourceException
     * @return ResponseEntity с ErrorResponse
     */
    @Operation(summary = "Обработка ошибки дублирования ресурса")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", description = "Конфликт: ресурс уже существует")
    })
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(ZonedDateTime.now(ZoneOffset.UTC))
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .exceptionType(ex.getClass().getSimpleName())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<ErrorResponse> handleInvalidParameter(InvalidParameterException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(ZonedDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .exceptionType(ex.getClass().getSimpleName())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }
}