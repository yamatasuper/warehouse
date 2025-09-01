package com.example.warehouse.camunda.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceCheckMessage {
    private String businessKey;
    private String login;
    private String inn;
    private Instant timestamp;
}