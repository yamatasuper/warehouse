package com.example.warehouse.camunda;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("createContractDelegate")
public class CreateContractDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // логика создания контракта
        execution.setVariable("contractId", "CONTRACT_123");
    }
}

