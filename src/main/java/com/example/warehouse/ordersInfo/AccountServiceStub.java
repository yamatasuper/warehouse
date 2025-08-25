package com.example.warehouse.ordersInfo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Profile("!prod") // Только для разработки и тестирования
@Slf4j
public class AccountServiceStub {

    private final Map<String, String> accountNumbers = Map.of(
            "user1", "12345678",
            "user2", "87654321",
            "user3", "55556666",
            "testuser", "99998888",
            "admin", "11112222"
    );

    @PostMapping("/api/accounts")
    public ResponseEntity<Map<String, String>> getAccountNumbers(@RequestBody Map<String, List<String>> request) {
        log.info("Account service stub called with: {}", request);

        List<String> logins = request.get("logins");
        Map<String, String> response = new HashMap<>();

        for (String login : logins) {
            response.put(login, accountNumbers.getOrDefault(login, "00000000"));
        }

        // Имитируем задержку 3 секунды
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return ResponseEntity.ok(response);
    }
}