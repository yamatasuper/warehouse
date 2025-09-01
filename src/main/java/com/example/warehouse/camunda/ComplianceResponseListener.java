package com.example.warehouse.camunda;

import com.example.warehouse.camunda.dto.ComplianceResponseMessage;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ComplianceResponseListener {

    private final RuntimeService runtimeService;

    @KafkaListener(topics = "compliance-check-responses", groupId = "order-service-group")
    public void handleComplianceResponse(ComplianceResponseMessage message) {
        log.info("Received compliance response for businessKey: {}", message.getBusinessKey());

        try {
            runtimeService.createMessageCorrelation("Message_ComplianceResponse")
                    .processInstanceBusinessKey(message.getBusinessKey())
                    .setVariable("complianceResult", message.getApproved())
                    .setVariable("rejectionReason", message.getRejectionReason())
                    .correlate();

        } catch (Exception e) {
            log.error("Failed to correlate compliance response for businessKey: {}",
                    message.getBusinessKey(), e);
        }
    }
}