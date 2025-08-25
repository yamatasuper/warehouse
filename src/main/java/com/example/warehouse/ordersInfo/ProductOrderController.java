package com.example.warehouse.ordersInfo;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/product-orders")
@RequiredArgsConstructor
public class ProductOrderController {

    private final ProductOrderService productOrderService;

    @GetMapping
    public ResponseEntity<Map<UUID, List<OrderInfo>>> getProductOrdersInfo() {
        Map<UUID, List<OrderInfo>> result = productOrderService.getProductOrdersInfo();
        return ResponseEntity.ok(result);
    }
}