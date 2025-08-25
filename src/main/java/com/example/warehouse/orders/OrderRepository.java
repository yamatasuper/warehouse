package com.example.warehouse.orders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    @Query("SELECT o FROM OrderEntity o WHERE o.id = :orderId AND o.customerId = :customerId")
    Optional<OrderEntity> findByIdAndCustomerId(@Param("orderId") UUID orderId,
                                                @Param("customerId") Long customerId);

    @Query("SELECT o FROM OrderEntity o WHERE o.status IN :statuses")
    List<OrderEntity> findByStatusIn(@Param("statuses") List<OrderStatus> statuses);
}