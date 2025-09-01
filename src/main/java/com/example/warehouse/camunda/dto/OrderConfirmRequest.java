package com.example.warehouse.camunda.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderConfirmRequest {
    @NotBlank
    private String inn;

    @NotBlank
    private String accountNumber;

    @NotBlank
    private String deliveryAddress;
}