package com.hyperlofy.backend.ai.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperlofy.backend.ai.config.AiGatewayProperties;
import com.hyperlofy.backend.ai.exception.AiProviderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiAiProvider implements AiProvider {

    private final RestClient aiGatewayRestClient;
    private final AiGatewayProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    public ProviderType providerType() {
        return ProviderType.GEMINI;
    }

    @Override
    public String generate(ProviderType providerType, String prompt, String model, String systemPrompt) {
        if (providerType != ProviderType.GEMINI) {
            throw new AiProviderException("Unsupported provider type: " + providerType);
        }

        String apiKey = properties.getGemini().getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new AiProviderException("Gemini API key is missing. Configure app.ai.gateway.gemini.api-key.");
        }

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> contents = new HashMap<>();
        contents.put("parts", List.of(Map.of("text", prompt)));

        Map<String, Object> systemInstruction = new HashMap<>();
        systemInstruction.put("parts", List.of(Map.of("text", systemPrompt)));

        requestBody.put("contents", List.of(contents));
        requestBody.put("system_instruction", systemInstruction);
        requestBody.put("generationConfig", Map.of("response_mime_type", "application/json"));

        String body;
        try {
            body = objectMapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException ex) {
            throw new AiProviderException("Failed to build Gemini request payload", ex);
        }

        try {
            ResponseEntity<String> response = aiGatewayRestClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/{model}:generateContent")
                            .queryParam("key", apiKey)
                            .build(model))
                    .body(body)
                    .retrieve()
                    .toEntity(String.class);

            if (response.getStatusCode().isError()) {
                throw new AiProviderException("Gemini provider returned HTTP " + response.getStatusCodeValue());
            }

            return response.getBody();
        } catch (Exception ex) {
            log.error("Gemini provider request failed", ex);
            throw new AiProviderException("Gemini provider request failed", ex);
        }
    }
}
