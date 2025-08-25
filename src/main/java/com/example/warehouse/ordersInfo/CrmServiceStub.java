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
public class CrmServiceStub {

    private final Map<String, String> inns = Map.of(
            "user1", "123456789012",
            "user2", "987654321098",
            "user3", "555555555555",
            "testuser", "999999999999",
            "admin", "111111111111"
    );

    @PostMapping("/api/customers/inn")
    public ResponseEntity<Map<String, String>> getInns(@RequestBody Map<String, List<String>> request) {
        log.info("CRM service stub called with: {}", request);

        List<String> logins = request.get("logins");
        Map<String, String> response = new HashMap<>();

        for (String login : logins) {
            response.put(login, inns.getOrDefault(login, "000000000000"));
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
