package com.example.warehouse.service.request;

import com.example.warehouse.enums.ProductCategoryEnum;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateProductCommand {
    private UUID id;
    private String name;
    private String description;
    private ProductCategoryEnum category;
    private BigDecimal price;
    private BigDecimal quantity;
}