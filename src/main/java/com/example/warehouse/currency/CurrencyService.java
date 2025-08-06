package com.example.warehouse.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyServiceClient currencyServiceClient;
    private final ObjectMapper objectMapper;

    @Value("classpath:exchange-rate.json")
    private Resource resourceFile;

    @Cacheable(value = "exchangeRates", condition = "#result != null")
    public ExchangeRatesResponse getExchangeRates() {
        try {
            return currencyServiceClient.getExchangeRates();
        } catch (Exception e) {
            try (InputStream is = resourceFile.getInputStream()) {
                return objectMapper.readValue(is, ExchangeRatesResponse.class);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to get exchange rates", ex);
            }
        }
    }

    public BigDecimal convertPrice(BigDecimal price, String fromCurrency, String toCurrency,
                                   ExchangeRatesResponse rates) {
        if (fromCurrency.equals(toCurrency)) {
            return price;
        }

        double rate = switch (toCurrency) {
            case "CNY" -> rates.getCnyRate();
            case "USD" -> rates.getUsdRate();
            case "EUR" -> rates.getEurRate();
            default -> 1.0;
        };

        return price.divide(BigDecimal.valueOf(rate), 2, RoundingMode.HALF_UP);
    }
}
