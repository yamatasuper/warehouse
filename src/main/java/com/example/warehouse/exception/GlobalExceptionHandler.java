package com.example.warehouse.exception;

import org.springframework.dao.InvalidDataAccessApiUsageException;
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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
     */
    @Operation(summary = "Обработка внутренних ошибок сервера")
    @ApiResponse(
            responseCode = "500",
            description = "Внутренняя ошибка сервера",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                {
                  "timestamp": "2023-05-20T12:34:56.789Z",
                  "status": 500,
                  "error": "Internal Server Error",
                  "message": "Произошла непредвиденная ошибка",
                  "exceptionType": "Exception"
                }
                """
                    )
            )
    )
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
     */
    @Operation(summary = "Обработка ошибок валидации")
    @ApiResponse(
            responseCode = "400",
            description = "Некорректный запрос",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                {
                  "timestamp": "2023-05-20T12:34:56.789Z",
                  "status": 400,
                  "error": "Bad Request",
                  "message": "Validation failed",
                  "exceptionType": "MethodArgumentNotValidException",
                  "details": ["fieldName: must not be blank"]
                }
                """
                    )
            )
    )
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
     */
    @Operation(summary = "Обработка ошибки дублирования ресурса")
    @ApiResponse(
            responseCode = "409",
            description = "Конфликт: ресурс уже существует",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                {
                  "timestamp": "2023-05-20T12:34:56.789Z",
                  "status": 409,
                  "error": "Conflict",
                  "message": "Ресурс с таким именем уже существует",
                  "exceptionType": "DuplicateResourceException"
                }
                """
                    )
            )
    )
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

    @Operation(summary = "Обработка неверного параметра")
    @ApiResponse(
            responseCode = "400",
            description = "Некорректный параметр",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                {
                  "timestamp": "2023-05-20T12:34:56.789Z",
                  "status": 400,
                  "error": "Bad Request",
                  "message": "Неверное значение параметра",
                  "exceptionType": "InvalidParameterException"
                }
                """
                    )
            )
    )
    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<ErrorResponse> handleInvalidParameter(InvalidParameterException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(ZonedDateTime.now(ZoneOffset.UTC))
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .exceptionType(ex.getClass().getSimpleName())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    // Добавьте обработчик для 404 ошибки, если у вас есть такой исключение
    @Operation(summary = "Обработка отсутствующего ресурса")
    @ApiResponse(
            responseCode = "404",
            description = "Ресурс не найден",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                {
                  "timestamp": "2023-05-20T12:34:56.789Z",
                  "status": 404,
                  "error": "Not Found",
                  "message": "Ресурс не найден",
                  "exceptionType": "ResourceNotFoundException"
                }
                """
                    )
            )
    )

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(ZonedDateTime.now(ZoneOffset.UTC))
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .exceptionType(ex.getClass().getSimpleName())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}