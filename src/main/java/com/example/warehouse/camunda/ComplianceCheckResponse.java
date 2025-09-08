package com.example.warehouse.camunda;

import lombok.Data;

@Data
public class ComplianceCheckResponse {
    private boolean approved;
    private String businessKey;
    private String reason;
}
