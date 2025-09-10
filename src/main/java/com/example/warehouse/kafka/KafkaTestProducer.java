package com.example.warehouse.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaTestProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.test-topic}")
    private String testTopic;

    public void sendTestMessage(String key, String message) throws JsonProcessingException {
        kafkaTemplate.send(testTopic, key, message.getBytes());
    }

    public void sendOrderEvent(String key, OrderEvent event) throws JsonProcessingException {
        byte[] value = objectMapper.writeValueAsBytes(event);
        kafkaTemplate.send("order_events", key, value);
    }
}