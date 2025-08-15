package com.example.warehouse.orders;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    // Дополнительные методы, если нужны
}