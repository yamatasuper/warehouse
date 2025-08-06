package com.example.warehouse.currency;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@ConditionalOnProperty(name = "currency.service.client", havingValue = "mock")
public class CurrencyServiceClientMock implements CurrencyServiceClient {
    @Override
    public ExchangeRatesResponse getExchangeRates() {
        Random random = new Random();
        return new ExchangeRatesResponse(
                random.nextDouble() * 10 + 10,
                random.nextDouble() * 50 + 50,
                random.nextDouble() * 50 + 50
        );
    }
}
