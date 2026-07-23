package com.hyperlofy.backend.ai.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(AiGatewayProperties.class)
public class AiGatewayConfiguration {

    @Bean
    public RestClient aiGatewayRestClient(AiGatewayProperties properties, ObjectMapper objectMapper) {
        ClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        ((SimpleClientHttpRequestFactory) requestFactory).setConnectTimeout(properties.getTimeoutMs());
        ((SimpleClientHttpRequestFactory) requestFactory).setReadTimeout(properties.getTimeoutMs());

        return RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(properties.getGemini().getBaseUrl())
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("X-Requested-With", "Hyperlofy-AI-Gateway")
                .build();
    }

    @Bean
    public RetryTemplate aiGatewayRetryTemplate(AiGatewayProperties properties) {
        RetryTemplate retryTemplate = new RetryTemplate();

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(properties.getMaxRetries() + 1);
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(Duration.ofMillis(300).toMillis());

        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return retryTemplate;
    }
}
