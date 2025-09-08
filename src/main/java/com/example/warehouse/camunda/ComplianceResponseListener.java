package com.example.warehouse.camunda;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import io.camunda.zeebe.client.ZeebeClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ComplianceResponseListener {

    private ZeebeClient zeebeClient;
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "compliance_responses", groupId = "order-service")
    public void handleComplianceResponse(@Payload byte[] message,
                                         @Header(KafkaHeaders.RECEIVED_KEY) String businessKey) {
        try {
            ComplianceCheckResponse response = objectMapper.readValue(message, ComplianceCheckResponse.class);

            Map<String, Object> variables = new HashMap<>();
            variables.put("complianceApproved", response.isApproved());

            zeebeClient.newPublishMessageCommand()
                    .messageName("ComplianceResponseMessage")
                    .correlationKey(businessKey)
                    .variables(variables)
                    .send()
                    .join();

            log.info("Processed compliance response for businessKey: {}, approved: {}",
                    businessKey, response.isApproved());

        } catch (Exception e) {
            log.error("Failed to process compliance response for businessKey: {}", businessKey, e);
        }
    }
}
