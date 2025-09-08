package com.example.warehouse.camunda.worker;

import com.example.warehouse.camunda.service.PaymentService;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentWorker {
    private final PaymentService paymentService;

    @ExternalTaskSubscription("make-payment")
    public void handle(ExternalTask task, ExternalTaskService taskService) {
        String orderId = task.getBusinessKey();
        paymentService.pay(orderId);
        taskService.complete(task);
    }
}

