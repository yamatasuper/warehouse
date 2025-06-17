package com.example.warehouse;

import com.example.warehouse.controller.ProductControllerImpl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения Warehouse Management System.
 * <p>
 * Запускает Spring Boot приложение для управления складом товаров.
 * Поддерживает два профиля работы:
 * </p>
 * <ul>
 *   <li><b>local</b> - с H2 in-memory базой данных</li>
 *   <li><b>prod</b> - с PostgreSQL (используется в Docker-контейнере)</li>
 * </ul>
 *
 * @see <a href="http://localhost:8080/swagger-ui.html">Swagger UI</a>
 * @see ProductControllerImpl
 */
@SpringBootApplication
public class WarehouseApplication {

	/**
	 * Точка входа в приложение.
	 *
	 * @param args аргументы командной строки (могут содержать настройки Spring Boot)
	 * @see <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config">Spring Boot Externalized Configuration</a>
	 */
	public static void main(String[] args) {
		SpringApplication.run(WarehouseApplication.class, args);
	}
}