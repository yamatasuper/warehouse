package com.example.warehouse.currency;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${currency-service.host}")
    private String currencyServiceHost;

    @Bean
    public WebClient currencyWebClient() {
        return WebClient.builder()
                .baseUrl(currencyServiceHost)
                .build();
    }
}
