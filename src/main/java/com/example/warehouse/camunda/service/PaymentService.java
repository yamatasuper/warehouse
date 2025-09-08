package com.example.warehouse.camunda.service;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    public void pay(String orderId) {
        System.out.println("💳 Payment done for order " + orderId);
    }
}
