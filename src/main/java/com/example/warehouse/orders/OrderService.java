package com.example.warehouse.orders;

import com.example.warehouse.kafka.OrderEvent;
import com.example.warehouse.kafka.OrderItemEvent;
import com.example.warehouse.kafka.OrderStatusUpdateEvent;
import com.example.warehouse.persistence.entity.ProductEntity;
import com.example.warehouse.persistence.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public UUID createOrder(Long customerId, OrderCreateRequest request) {
        // Проверяем существование покупателя
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }

        // Создаем заказ
        OrderEntity order = new OrderEntity();
        order.setId(UUID.randomUUID());
        order.setCustomerId(customerId);
        order.setStatus(OrderStatus.CREATED);
        order.setDeliveryAddress(request.getDeliveryAddress());

        OrderEntity savedOrder = orderRepository.save(order);

        // Добавляем товары в заказ
        for (OrderItemRequest item : request.getProducts()) {
            addOrderItem(savedOrder, item);
        }

        // Отправка события CREATE_ORDER в Kafka
        sendOrderEvent(savedOrder, "CREATE_ORDER", request.getProducts());

        return savedOrder.getId();
    }

    private void addOrderItem(OrderEntity order, OrderItemRequest item) {
        ProductEntity product = productRepository.findById(item.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + item.getId()));

        if (!product.getIsAvailable()) {
            throw new BusinessException("Product is not available: " + product.getId());
        }

        if (product.getQuantity().compareTo(item.getQuantity()) < 0) {
            throw new BusinessException("Not enough quantity for product: " + product.getId());
        }

        // Создаем позицию заказа
        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setId(UUID.randomUUID());
        orderItem.setOrderId(order.getId());
        orderItem.setProductId(product.getId());
        orderItem.setQuantity(item.getQuantity());
        orderItem.setPrice(product.getPrice());
        orderItemRepository.save(orderItem);

        // Уменьшаем количество товара на складе
        product.setQuantity(product.getQuantity().subtract(item.getQuantity()));
        productRepository.save(product);
    }

    public OrderResponse getOrder(UUID orderId, Long customerId) {
        // Используем JOIN FETCH для избежания N+1 проблемы
        OrderEntity order = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found or access denied"));

        // Загружаем все items с продуктами одним запросом
        List<OrderItemProjection> items = orderItemRepository.findByOrderIdWithProduct(orderId);

        BigDecimal totalPrice = items.stream()
                .map(item -> item.getPrice().multiply(item.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return OrderResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .deliveryAddress(order.getDeliveryAddress())
                .createdAt(order.getCreatedAt())
                .products(items.stream()
                        .map(item -> OrderProductResponse.builder()
                                .productId(item.getProductId())
                                .name(item.getProductName())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .build())
                        .toList())
                .totalPrice(totalPrice)
                .build();
    }

    public void cancelOrder(UUID orderId, Long customerId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getCustomerId().equals(customerId)) {
            throw new BusinessException("Access denied to cancel order");
        }

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new BusinessException("Only orders with CREATED status can be cancelled");
        }

        // Возвращаем товары на склад
        List<OrderItemEntity> items = orderItemRepository.findByOrderId(orderId);
        for (OrderItemEntity item : items) {
            ProductEntity product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + item.getProductId()));

            product.setQuantity(product.getQuantity().add(item.getQuantity()));
            productRepository.save(product);
        }

        // Меняем статус заказа
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // Отправка события CANCEL_ORDER в Kafka
        sendOrderEvent(order, "CANCEL_ORDER", null);
    }

    public void updateOrderStatus(UUID orderId, OrderStatusUpdateRequest request) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        OrderStatus oldStatus = order.getStatus();
        OrderStatus newStatus = OrderStatus.valueOf(request.getStatus());

        order.setStatus(newStatus);
        orderRepository.save(order);

        // Отправка события UPDATE_ORDER_STATUS в Kafka
        sendOrderStatusUpdateEvent(order, oldStatus, newStatus);
    }

    private void sendOrderEvent(OrderEntity order, String eventType, List<OrderItemRequest> products) {
        try {
            OrderEvent event = new OrderEvent();
            event.setEvent(eventType);
            event.setOrderId(order.getId());
            event.setCustomerId(order.getCustomerId());
            event.setDeliveryAddress(order.getDeliveryAddress());
            event.setStatus(order.getStatus().name());

            if (products != null) {
                List<OrderItemEvent> itemEvents = products.stream()
                        .map(item -> new OrderItemEvent(item.getId(), item.getQuantity()))
                        .collect(Collectors.toList());
                event.setProducts(itemEvents);
            }

            byte[] value = objectMapper.writeValueAsBytes(event);
            kafkaTemplate.send("order_events", order.getId().toString(), value);
            log.info("Sent {} event for order: {}", eventType, order.getId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize order event for order: {}", order.getId(), e);
        }
    }

    private void sendOrderStatusUpdateEvent(OrderEntity order, OrderStatus oldStatus, OrderStatus newStatus) {
        try {
            OrderStatusUpdateEvent event = new OrderStatusUpdateEvent();
            event.setEvent("UPDATE_ORDER_STATUS");
            event.setOrderId(order.getId());
            event.setCustomerId(order.getCustomerId());
            event.setOldStatus(oldStatus.name());
            event.setNewStatus(newStatus.name());
            event.setTimestamp(Instant.now());

            byte[] value = objectMapper.writeValueAsBytes(event);
            kafkaTemplate.send("order_status_events", order.getId().toString(), value);
            log.info("Sent UPDATE_ORDER_STATUS event for order: {} ({} -> {})",
                    order.getId(), oldStatus, newStatus);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize order status update event for order: {}", order.getId(), e);
        }
    }
}