package com.example.warehouse.orders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, UUID> {
    List<OrderItemEntity> findByOrderId(UUID orderId);

    @Query("SELECT oi.id as id, oi.orderId as orderId, oi.productId as productId, " +
            "oi.quantity as quantity, oi.price as price, p.name as productName " +
            "FROM OrderItemEntity oi JOIN ProductEntity p ON oi.productId = p.id " +
            "WHERE oi.orderId = :orderId")
    List<OrderItemProjection> findByOrderIdWithProduct(UUID orderId);

    @Query("SELECT oi FROM OrderItemEntity oi WHERE oi.orderId IN :orderIds")
    List<OrderItemEntity> findByOrderIds(@Param("orderIds") List<UUID> orderIds);
}