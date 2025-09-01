package com.example.warehouse.camunda.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDeliveryResponse {
    private String deliveryId;
    private ZonedDateTime deliveryDate;
    private String status;
}