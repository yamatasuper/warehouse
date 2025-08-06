package com.example.warehouse.currency;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExchangeRatesResponse {
    @JsonProperty("exchangeRateCNY")
    private double cnyRate;

    @JsonProperty("exchangeRateUSD")
    private double usdRate;

    @JsonProperty("exchangeRateEUR")
    private double eurRate;
}
