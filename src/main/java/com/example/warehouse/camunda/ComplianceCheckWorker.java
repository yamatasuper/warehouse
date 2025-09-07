package com.example.warehouse.camunda;

import org.camunda.bpm.engine.delegate.DelegateExecution;
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

    private final OrderOrchestrationService orchestrationService;
    private final ZeebeClient zeebeClient;

    // Для Camunda
    public void checkCompliance(DelegateExecution execution) {
        String businessKey = (String) execution.getVariable("businessKey");
        Boolean approved = true; // можно логика проверки
        execution.setVariable("complianceApproved", approved);
    }

    // Для Zeebe
    @JobWorker(type = "SendComplianceCheck")
    public void handleComplianceCheckTask(final ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();
        String businessKey = (String) variables.get("businessKey");
        String login = (String) variables.get("login");
        String inn = (String) variables.get("inn");

        try {
            orchestrationService.sendComplianceCheck(businessKey, login, inn);
            zeebeClient.newCompleteCommand(job.getKey()).send().join();
        } catch (Exception e) {
            zeebeClient.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage(e.getMessage())
                    .send()
                    .join();
        }
    }
}
