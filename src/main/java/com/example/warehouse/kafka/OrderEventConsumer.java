package com.example.warehouse.kafka;

import com.example.warehouse.orders.OrderCreateRequest;
import com.example.warehouse.orders.OrderItemRequest;
import com.example.warehouse.orders.OrderService;
import com.example.warehouse.orders.OrderStatusUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @KafkaListener(topics = "${app.kafka.topics.test-topic}", groupId = "order-service-group")
    public void consumeTestMessage(ConsumerRecord<String, byte[]> record) {
        String message = new String(record.value());
        log.info("Received test message: key={}, value={}", record.key(), message);
    }

    @KafkaListener(topics = "order_events", groupId = "order-service-group")
    public void consumeOrderEvent(ConsumerRecord<String, byte[]> record) {
        try {
            OrderEvent event = objectMapper.readValue(record.value(), OrderEvent.class);
            log.info("Received order event: key={}, event={}", record.key(), event.getEvent());

            switch (event.getEvent()) {
                case "CREATE_ORDER":
                    handleCreateOrder(event);
                    break;
                case "UPDATE_ORDER":
                    handleUpdateOrder(event);
                    break;
                case "DELETE_ORDER":
                    handleDeleteOrder(event);
                    break;
                case "UPDATE_ORDER_STATUS":
                    handleUpdateOrderStatus(event);
                    break;
                default:
                    log.warn("Unknown event type: {}", event.getEvent());
            }
        } catch (Exception e) {
            log.error("Error processing order event", e);
        }
    }

    private void handleCreateOrder(OrderEvent event) {
        // Преобразование события в OrderCreateRequest
        OrderCreateRequest request = new OrderCreateRequest();
        request.setDeliveryAddress(event.getDeliveryAddress());

        List<OrderItemRequest> items = event.getProducts().stream()
                .map(item -> {
                    OrderItemRequest itemRequest = new OrderItemRequest();
                    itemRequest.setId(item.getId());
                    itemRequest.setQuantity(item.getQuantity());
                    return itemRequest;
                })
                .collect(Collectors.toList());

        request.setProducts(items);

        // Создание заказа
        orderService.createOrder(event.getCustomerId(), request);
    }

    private void handleUpdateOrder(OrderEvent event) {
        // Логика обновления заказа
        log.info("Updating order: {}", event.getOrderId());
    }

    private void handleDeleteOrder(OrderEvent event) {
        // Логика удаления заказа
        orderService.cancelOrder(event.getOrderId(), event.getCustomerId());
    }

    private void handleUpdateOrderStatus(OrderEvent event) {
        // Логика обновления статуса
        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest();
        request.setStatus(event.getStatus());
        orderService.updateOrderStatus(event.getOrderId(), request);
    }
}
