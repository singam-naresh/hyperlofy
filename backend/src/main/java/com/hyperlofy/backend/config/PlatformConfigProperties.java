package com.hyperlofy.backend.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "hyperlofy")
@Validated
@Getter
@Setter
public class PlatformConfigProperties {

    private AuthConfig auth = new AuthConfig();
    private MerchantConfig merchant = new MerchantConfig();
    private MarketplaceConfig marketplace = new MarketplaceConfig();
    private GatewayConfig gateway = new GatewayConfig();

    @Getter
    @Setter
    public static class AuthConfig {
        @Min(1)
        private int maxLoginAttempts = 5;

        @Min(60)
        private int otpExpirationSeconds = 300;

        @Min(300)
        private int sessionTimeoutSeconds = 86400;
    }

    @Getter
    @Setter
    public static class MerchantConfig {
        private double maxDeliveryRadiusKm = 15.0;
        private int notificationRetryCount = 3;
    }

    @Getter
    @Setter
    public static class MarketplaceConfig {
        @Min(1)
        private int inventoryReservationTimeoutMinutes = 15;
        private int lowStockThresholdDefault = 5;
    }

    @Getter
    @Setter
    public static class GatewayConfig {
        @NotBlank
        private String correlationHeaderName = "X-Correlation-ID";

        @NotBlank
        private String traceHeaderName = "X-Trace-ID";
        private int defaultRateLimitPerSecond = 100;
    }
}
