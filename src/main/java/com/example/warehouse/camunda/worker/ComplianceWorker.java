package com.example.warehouse.camunda.worker;

import com.example.warehouse.camunda.service.ComplianceService;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ComplianceWorker {
    private final ComplianceService complianceService;

    @ExternalTaskSubscription("compliance-check")
    public void handle(ExternalTask task, ExternalTaskService taskService) {
        String orderId = task.getBusinessKey();
        complianceService.check(orderId);
        taskService.complete(task);
    }
}

