package com.example.warehouse.camunda.worker;

import com.example.warehouse.camunda.service.ContractService;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ContractWorker {
    private final ContractService contractService;

    @ExternalTaskSubscription("register-contract")
    public void handle(ExternalTask task, ExternalTaskService taskService) {
        String orderId = task.getBusinessKey();
        contractService.register(orderId);
        taskService.complete(task);
    }
}

