package com.example.warehouse.camunda.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterContractResponse {
    private String contractId;
    private String status;
    private String message;
}
