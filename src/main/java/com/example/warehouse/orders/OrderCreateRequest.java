package com.example.warehouse.orders;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class OrderCreateRequest {
    @NotBlank
    private String deliveryAddress;

    @NotEmpty
    private List<OrderItemRequest> products;
}
