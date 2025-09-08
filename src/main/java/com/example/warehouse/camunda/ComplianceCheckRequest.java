package com.example.warehouse.camunda;

import lombok.Data;

@Data
public class ComplianceCheckRequest {
    private String login;
    private String inn;
    private String businessKey;
}
