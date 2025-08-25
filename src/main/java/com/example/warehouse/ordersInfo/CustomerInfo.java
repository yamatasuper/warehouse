package com.example.warehouse.ordersInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInfo {
    private Long id;
    private String accountNumber;
    private String email;
    private String inn;
}