package com.example.warehouse.ordersInfo;

import com.example.warehouse.orders.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final AccountServiceClient accountServiceClient;
    private final CrmServiceClient crmServiceClient;

    public Map<UUID, List<OrderInfo>> getProductOrdersInfo() {
        // 1. Получаем все заказы в статусах CREATED и CONFIRMED
        List<OrderEntity> orders = orderRepository.findByStatusIn(
                List.of(OrderStatus.CREATED, OrderStatus.CONFIRMED)
        );

        // 2. Собираем уникальные логины клиентов
        Set<String> customerLogins = orders.stream()
                .map(OrderEntity::getCustomerId)
                .map(customerId -> customerRepository.findById(customerId)
                        .map(CustomerEntity::getLogin)
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<String> loginList = new ArrayList<>(customerLogins);

        // 3. Асинхронно запрашиваем данные из внешних сервисов
        CompletableFuture<Map<String, String>> accountNumbersFuture =
                accountServiceClient.getAccountNumbers(loginList);

        CompletableFuture<Map<String, String>> innsFuture =
                crmServiceClient.getInns(loginList);

        // 4. Ждем завершения всех асинхронных запросов
        CompletableFuture.allOf(accountNumbersFuture, innsFuture).join();

        Map<String, String> accountNumbers;
        Map<String, String> inns;

        try {
            accountNumbers = accountNumbersFuture.get(5, TimeUnit.SECONDS);
            inns = innsFuture.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Error getting external data", e);
            accountNumbers = Collections.emptyMap();
            inns = Collections.emptyMap();
        }

        // 5. Создаем мапу для быстрого доступа к информации о клиентах
        Map<Long, CustomerInfo> customerInfoMap = createCustomerInfoMap(
                orders, accountNumbers, inns
        );

        // 6. Собираем информацию о товарах и заказах
        return collectProductOrdersInfo(orders, customerInfoMap);
    }

    private Map<Long, CustomerInfo> createCustomerInfoMap(
            List<OrderEntity> orders,
            Map<String, String> accountNumbers,
            Map<String, String> inns
    ) {
        Map<Long, CustomerInfo> customerInfoMap = new HashMap<>();

        for (OrderEntity order : orders) {
            CustomerEntity customer = customerRepository.findById(order.getCustomerId())
                    .orElse(null);

            if (customer != null) {
                CustomerInfo customerInfo = CustomerInfo.builder()
                        .id(customer.getId())
                        .email(customer.getEmail())
                        .accountNumber(accountNumbers.getOrDefault(customer.getLogin(), "N/A"))
                        .inn(inns.getOrDefault(customer.getLogin(), "N/A"))
                        .build();

                customerInfoMap.put(customer.getId(), customerInfo);
            }
        }

        return customerInfoMap;
    }

    private Map<UUID, List<OrderInfo>> collectProductOrdersInfo(
            List<OrderEntity> orders,
            Map<Long, CustomerInfo> customerInfoMap
    ) {
        Map<UUID, List<OrderInfo>> result = new HashMap<>();

        for (OrderEntity order : orders) {
            // Получаем все товары в заказе
            List<OrderItemEntity> orderItems = orderItemRepository.findByOrderId(order.getId());

            for (OrderItemEntity item : orderItems) {
                OrderInfo orderInfo = OrderInfo.builder()
                        .id(order.getId())
                        .customer(customerInfoMap.get(order.getCustomerId()))
                        .status(order.getStatus())
                        .deliveryAddress(order.getDeliveryAddress())
                        .quantity(item.getQuantity().intValue())
                        .build();

                result.computeIfAbsent(item.getProductId(), k -> new ArrayList<>())
                        .add(orderInfo);
            }
        }

        return result;
    }
}