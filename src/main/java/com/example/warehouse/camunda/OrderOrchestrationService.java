package com.example.warehouse.camunda;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderOrchestrationService {

    private final RuntimeService runtimeService;

    public ProcessInstance startOrderConfirmationProcess(String orderId) {
        return runtimeService.startProcessInstanceByKey(
                "order-orchestration",  // Process Id из BPMN
                orderId                 // businessKey = ID заказа
        );
    }
}

