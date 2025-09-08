package com.example.warehouse.camunda.worker;

import com.example.warehouse.camunda.service.DeliveryService;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeliveryWorker {
    private final DeliveryService deliveryService;

    @ExternalTaskSubscription("register-delivery")
    public void handle(ExternalTask task, ExternalTaskService taskService) {
        String orderId = task.getBusinessKey();
        deliveryService.register(orderId);
        taskService.complete(task);
    }
}

