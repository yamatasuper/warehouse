package com.example.warehouse.search;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Критерий поиска")
public class SearchCriteria {
    @NotBlank(message = "Поле 'field' не может быть пустым")
    @Schema(description = "Поле для фильтрации", example = "price")
    private String field;

    @NotNull(message = "Поле 'value' не может быть null")
    @Schema(description = "Значение для сравнения", example = "100.0")
    private Object value;

    @NotBlank(message = "Поле 'operation' не может быть пустым")
    @ValidSearchOperation
    @Schema(description = "Операция сравнения (=, >=, <=, ~, EQUAL, GRATER_THAN_OR_EQ, LESS_THAN_OR_EQ, LIKE)",
            example = ">=")
    private String operation;
}
