package com.example.warehouse.camunda.service;

import org.springframework.stereotype.Service;

@Service
public class DeliveryService {
    public void register(String orderId) {
        System.out.println("🚚 Delivery registered for order " + orderId);
    }
}
