package com.hyperlofy.backend.ai.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperlofy.backend.ai.exception.AiResponseParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiResponseParser {

    private final ObjectMapper objectMapper;

    public Object parse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode candidate = root.path("candidates").get(0);
            JsonNode content = candidate.path("content").path("parts").get(0).path("text");
            String text = content.asText();

            if (text == null || text.isBlank()) {
                throw new AiResponseParseException("AI provider returned an empty response body");
            }

            try {
                return objectMapper.readValue(text, Object.class);
            } catch (JsonProcessingException ignored) {
                return text;
            }
        } catch (Exception ex) {
            throw new AiResponseParseException("Failed to parse provider response", ex);
        }
    }
}
