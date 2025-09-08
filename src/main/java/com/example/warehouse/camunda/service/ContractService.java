package com.example.warehouse.camunda.service;

import org.springframework.stereotype.Service;

@Service
public class ContractService {
    public void register(String orderId) {
        System.out.println("📑 Contract registered for order " + orderId);
    }
}
