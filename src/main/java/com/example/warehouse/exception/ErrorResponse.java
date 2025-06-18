package com.example.warehouse.exception;

import java.time.ZonedDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Стандартизированный ответ об ошибке для REST API.
 * Содержит информацию об ошибке, включая временную метку, HTTP статус,
 * сообщение об ошибке и дополнительные детали.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Стандартизированный ответ об ошибке для REST API")
public class ErrorResponse {
    /**
     * Временная метка возникновения ошибки в формате ISO-8601.
     */
    @Schema(description = "Временная метка возникновения ошибки", example = "2023-05-20T12:34:56.789Z")
    private ZonedDateTime timestamp;

    /**
     * HTTP статус код ошибки.
     */
    @NotNull
    @Schema(description = "HTTP статус код", example = "404")
    private int status;

    /**
     * Текстовое описание HTTP статуса.
     */
    @Schema(description = "Описание HTTP статуса", example = "Not Found")
    private String error;

    /**
     * Основное сообщение об ошибке, предназначенное для пользователя.
     */
    @NotBlank
    @Schema(description = "Сообщение об ошибке", example = "Ресурс не найден")
    private String message;

    /**
     * Тип исключения, которое вызвало ошибку.
     */
    @Schema(description = "Тип исключения", example = "ResourceNotFoundException")
    private String exceptionType;

    /**
     * Дополнительные детали ошибки, такие как ошибки валидации или stack trace.
     */
    @Schema(description = "Детали ошибки (например, ошибки валидации)")
    private List<String> details;
}