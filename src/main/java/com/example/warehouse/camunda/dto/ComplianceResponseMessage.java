package com.example.warehouse.camunda.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceResponseMessage {
    private String businessKey;
    private Boolean approved;
    private String rejectionReason;
    private Instant timestamp;
}
