package com.example.warehouse.camunda;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ComplianceCheckWorker {

    private ZeebeClient zeebeClient;
    private KafkaTemplate<String, byte[]> kafkaTemplate;
    private ObjectMapper objectMapper;

    @JobWorker(type = "compliance-check", autoComplete = true)
    public void handleComplianceCheck(final ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();
        String businessKey = (String) variables.get("businessKey");
        String login = (String) variables.get("login");
        String inn = (String) variables.get("inn");

        // Отправляем запрос в Kafka
        try {
            ComplianceCheckRequest request = new ComplianceCheckRequest();
            request.setLogin(login);
            request.setInn(inn);
            request.setBusinessKey(businessKey);

            byte[] value = objectMapper.writeValueAsBytes(request);
            kafkaTemplate.send("compliance_requests", businessKey, value);

            log.info("Sent compliance check request for businessKey: {}", businessKey);
        } catch (Exception e) {
            log.error("Failed to send compliance check request", e);
            throw new RuntimeException("Compliance check failed", e);
        }
    }
}