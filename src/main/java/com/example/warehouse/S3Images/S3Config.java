package com.example.warehouse.S3Images;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@EnableConfigurationProperties(S3Properties.class)
public class S3Config {

    @Bean
    @ConditionalOnProperty(name = "aws.s3.enabled", havingValue = "true", matchIfMissing = true)
    public S3Client s3Client(S3Properties s3Properties) {
        if (s3Properties.getAccessKey() == null || s3Properties.getSecretKey() == null) {
            // Handle missing credentials appropriately
            throw new IllegalStateException("AWS credentials are required when S3 is enabled");
        }

        return S3Client.builder()
                .endpointOverride(URI.create(s3Properties.getEndpoint()))
                .region(Region.of(s3Properties.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        s3Properties.getAccessKey(), s3Properties.getSecretKey())))
                .serviceConfiguration(b -> b.pathStyleAccessEnabled(true))
                .build();
    }
}