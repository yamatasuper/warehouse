package com.example.warehouse.camunda;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("processPaymentDelegate")
public class ProcessPaymentDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        // Логика обработки платежа
        execution.setVariable("paymentSuccess", true);
    }
}

