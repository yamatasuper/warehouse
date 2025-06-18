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
 * Содержит информацию о времени возникновения, статусе, типе ошибки и детали.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Стандартизированный ответ об ошибке для REST API")
public class ErrorResponse {
    @Schema(description = "Временная метка возникновения ошибки", example = "2023-05-20T12:34:56.789Z")
    private ZonedDateTime timestamp;

    @NotNull
    @Schema(description = "HTTP статус код", example = "404")
    private int status;

    @Schema(description = "Описание HTTP статуса", example = "Not Found")
    private String error;

    @NotBlank
    @Schema(description = "Сообщение об ошибке", example = "Ресурс не найден")
    private String message;

    @Schema(description = "Тип исключения", example = "ResourceNotFoundException")
    private String exceptionType;

    @Schema(description = "Детали ошибки (например, ошибки валидации)")
    private List<String> details;
}