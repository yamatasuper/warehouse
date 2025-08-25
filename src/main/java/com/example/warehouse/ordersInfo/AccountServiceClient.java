package com.example.warehouse.ordersInfo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class AccountServiceClient {

    private final RestTemplate restTemplate;

    public AccountServiceClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(4))
                .setReadTimeout(Duration.ofSeconds(4))
                .build();
    }

    public CompletableFuture<Map<String, String>> getAccountNumbers(List<String> logins) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Requesting account numbers for logins: {}", logins);

                String url = "http://account-service/api/accounts";
                Map<String, List<String>> request = Map.of("logins", logins);

                ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    return response.getBody();
                }
                return Collections.emptyMap();

            } catch (Exception e) {
                log.error("Error fetching account numbers", e);
                return Collections.emptyMap();
            }
        });
    }
}
