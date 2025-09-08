package com.example.warehouse.camunda;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderConfirmRequest {
    @NotBlank
    private String deliveryAddress;

    @NotBlank
    private String inn;

    @NotBlank
    private String accountNumber;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal totalAmount;

    @NotBlank
    private String login;
}