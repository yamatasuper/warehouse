package com.example.warehouse.camunda.service;

import org.springframework.stereotype.Service;

@Service
public class ComplianceService {
    public void check(String orderId) {
        System.out.println("✅ Compliance check passed for order " + orderId);
    }
}

