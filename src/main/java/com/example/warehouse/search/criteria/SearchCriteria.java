package com.example.warehouse.search.criteria;

import com.example.warehouse.search.operation.ValidSearchOperation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Класс, представляющий критерий поиска для фильтрации данных.
 * Используется для указания поля, значения и операции сравнения.
 */
@Data
@Schema(description = "Критерий поиска")
public class SearchCriteria {
    /**
     * Название поля сущности, по которому производится фильтрация.
     * Не может быть пустым.
     */
    @NotBlank(message = "Поле 'field' не может быть пустым")
    @Schema(description = "Поле для фильтрации", example = "price")
    private final String field;

    /**
     * Значение для сравнения с указанным полем.
     * Не может быть null.
     */
    @NotNull(message = "Поле 'value' не может быть null")
    @Schema(description = "Значение для сравнения", example = "100.0")
    private final Object value;

    /**
     * Операция сравнения для фильтрации.
     * Поддерживаемые операции: =, >=, <=, ~, EQUAL, GRATER_THAN_OR_EQ, LESS_THAN_OR_EQ, LIKE.
     * Не может быть пустым и должна быть одной из допустимых операций.
     */
    @NotBlank(message = "Поле 'operation' не может быть пустым")
    @ValidSearchOperation
    @Schema(description = "Операция сравнения (=, >=, <=, ~, EQUAL, GRATER_THAN_OR_EQ, LESS_THAN_OR_EQ, LIKE)",
            example = ">=")
    private final String operation;
}
