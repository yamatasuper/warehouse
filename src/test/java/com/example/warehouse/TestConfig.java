package com.example.warehouse;

import com.example.warehouse.camunda.ComplianceCheckWorker;
import com.example.warehouse.currency.CurrencyFilter;
import com.example.warehouse.currency.CurrencyService;
import com.example.warehouse.currency.CurrencyServiceClient;
import io.camunda.zeebe.client.ZeebeClient;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import software.amazon.awssdk.services.s3.S3Client;

@TestConfiguration
public class TestConfig {

    // ... your existing mock beans ...

    @Bean
    @Primary
    public CurrencyServiceClient currencyServiceClient() {
        return Mockito.mock(CurrencyServiceClient.class);
    }

    @Bean
    @Primary
    public CurrencyService currencyService() {
        return Mockito.mock(CurrencyService.class);
    }

    @Bean
    @Primary
    public CurrencyFilter currencyFilter() {
        return Mockito.mock(CurrencyFilter.class);
    }

    @Bean
    @Primary
    public S3Client s3Client() {
        return Mockito.mock(S3Client.class);
    }

    @Bean
    @Primary
    public com.example.warehouse.S3Images.S3Service s3Service() {
        return Mockito.mock(com.example.warehouse.S3Images.S3Service.class);
    }

    @Bean
    @Primary
    public ZeebeClient zeebeClient() {
        return Mockito.mock(ZeebeClient.class);
    }

    @Bean
    @Primary
    public ComplianceCheckWorker complianceCheckWorker() {
        return Mockito.mock(ComplianceCheckWorker.class);
    }

    // Add ALL the required KafkaTemplate mocks
    @Bean
    @Primary
    public KafkaTemplate<String, String> kafkaTemplate() {
        return Mockito.mock(KafkaTemplate.class);
    }

    @Bean
    @Primary
    public KafkaTemplate<String, byte[]> kafkaTemplateByteArray() {
        return Mockito.mock(KafkaTemplate.class);
    }

    @Bean
    @Primary
    public KafkaTemplate<String, Object> kafkaObjectTemplate() {
        return Mockito.mock(KafkaTemplate.class);
    }

    // If you have any other Kafka-related services, add them here
    @Bean
    @Primary
    public org.springframework.kafka.config.KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry() {
        return Mockito.mock(org.springframework.kafka.config.KafkaListenerEndpointRegistry.class);
    }
}