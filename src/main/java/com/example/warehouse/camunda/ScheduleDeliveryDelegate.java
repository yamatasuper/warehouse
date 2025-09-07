package com.example.warehouse.camunda;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component("scheduleDeliveryDelegate")
public class ScheduleDeliveryDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        // Логика планирования доставки
        execution.setVariable("deliveryDate", Instant.parse("2024-01-15T10:00:00Z"));
    }
}
