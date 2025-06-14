package com.example.warehouse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Конфигурация Swagger/OpenAPI для документации API.
 * <p>
 * Доступно через: http://localhost:8080/swagger-ui.html
 * </p>
 */
@Configuration
public class SwaggerConfig {

    /**
     * Настройка OpenAPI документации.
     * @return конфигурация OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Warehouse API")
                        .version("1.0")
                        .description("API для управления складом товаров"));
    }
}