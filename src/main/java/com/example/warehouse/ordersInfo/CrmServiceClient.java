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
public class CrmServiceClient {

    private final RestTemplate restTemplate;

    public CrmServiceClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(4))
                .setReadTimeout(Duration.ofSeconds(4))
                .build();
    }

    public CompletableFuture<Map<String, String>> getInns(List<String> logins) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Requesting INNs for logins: {}", logins);

                String url = "http://crm-service/api/customers/inn";
                Map<String, List<String>> request = Map.of("logins", logins);

                ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    return response.getBody();
                }
                return Collections.emptyMap();

            } catch (Exception e) {
                log.error("Error fetching INNs", e);
                return Collections.emptyMap();
            }
        });
    }
}
