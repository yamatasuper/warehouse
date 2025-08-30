package kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/kafka-test")
@RequiredArgsConstructor
public class KafkaTestController {

    private final KafkaTestProducer kafkaProducer;

    @PostMapping("/test-message")
    public ResponseEntity<Void> sendTestMessage(
            @RequestParam String key,
            @RequestParam String message) throws JsonProcessingException {
        kafkaProducer.sendTestMessage(key, message);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/order-event")
    public ResponseEntity<Void> sendOrderEvent(@RequestBody OrderEvent event) throws JsonProcessingException {
        String key = event.getOrderId() != null ? event.getOrderId().toString() : UUID.randomUUID().toString();
        kafkaProducer.sendOrderEvent(key, event);
        return ResponseEntity.ok().build();
    }
}