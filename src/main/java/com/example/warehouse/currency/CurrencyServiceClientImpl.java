package com.example.warehouse.currency;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "currency.service.client", havingValue = "real")
public class CurrencyServiceClientImpl implements CurrencyServiceClient {
    private final WebClient webClient;

    @Value("${currency-service.methods.get-currency}")
    private String getCurrencyEndpoint;

    @Override
    public ExchangeRatesResponse getExchangeRates() {
        return webClient.get()
                .uri(getCurrencyEndpoint)
                .retrieve()
                .bodyToMono(ExchangeRatesResponse.class)
                .retry(2)
                .block();
    }
}
