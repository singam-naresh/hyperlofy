package com.hyperlofy.backend.ai.shared;

import com.hyperlofy.backend.ai.config.AiGatewayProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiGatewayHealthIndicator implements HealthIndicator {

    private final AiGatewayProperties properties;

    @Override
    public Health health() {
        if (properties.getGemini().getApiKey() == null || properties.getGemini().getApiKey().isBlank()) {
            return Health.down()
                    .withDetail("provider", "GEMINI")
                    .withDetail("status", "CONFIGURATION_MISSING")
                    .build();
        }

        return Health.up()
                .withDetail("provider", "GEMINI")
                .withDetail("status", "READY")
                .build();
    }
}
