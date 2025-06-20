package com.example.warehouse.task_1.schedulers;

/**
 * Интерфейс для сервиса обновления цен продуктов.
 * Определяет контракт для периодического обновления цен.
 */
public interface ProductPriceScheduler {
    /**
     * Метод для выполнения обновления цен продуктов.
     * Реализация должна содержать логику периодического выполнения.
     */
    void updateProductPrices();
}