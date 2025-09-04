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
@Schema(description = "Стандартизированный ответ об ошибке для REST API")
public class ErrorResponse {
    @Schema(description = "Детали ошибки")
    private final ErrorDetails errorDetails;

    @Schema(description = "HTTP статус код", example = "404")
    private final int status;

    @Schema(description = "Описание HTTP статуса", example = "Not Found")
    private final String error;
}