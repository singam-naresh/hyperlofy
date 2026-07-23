package com.hyperlofy.backend.ai.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.ai.gateway")
public class AiGatewayProperties {

    @NotNull
    private ProviderProperties gemini = new ProviderProperties();

    @NotBlank
    private String defaultProvider = "GEMINI";

    @NotBlank
    private String defaultModel = "gemini-2.5-flash";

    @NotBlank
    private String systemPrompt = "You are Hyperlofy AI Gateway. Return only valid JSON objects.";

    @NotNull
    private Integer timeoutMs = 10000;

    @NotNull
    private Integer maxRetries = 2;

    @NotBlank
    private String responseSchema = "object";

    @Getter
    @Setter
    public static class ProviderProperties {
        private String apiKey = "";

        @NotBlank
        private String baseUrl = "https://generativelanguage.googleapis.com";

        @NotBlank
        private String model = "gemini-2.5-flash";
    }
}
