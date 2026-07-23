package com.hyperlofy.backend.ai.intent;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class IntentParser {

    private final ObjectMapper objectMapper;
    private final IntentResponseValidator intentResponseValidator;

    public IntentResponse parse(Object content) {
        if (content == null) {
            throw new IllegalArgumentException("AI response content is empty");
        }

        Map<String, Object> payload = extractPayload(content);
        Map<String, Object> entities = new HashMap<>();
        Object rawEntities = payload.get("entities");
        if (rawEntities instanceof Map<?, ?> map) {
            map.forEach((key, value) -> entities.put(String.valueOf(key), value));
        }

        IntentResponse response = IntentResponse.builder()
                .intent(parseIntent((String) payload.getOrDefault("intent", "UNKNOWN")))
                .plan(parsePlan((String) payload.getOrDefault("plan", "REJECTED")))
                .confidence(toDouble(payload.get("confidence"), 0.0))
                .requiresConversation(Boolean.TRUE.equals(payload.get("requiresConversation")))
                .requiresVerification(Boolean.TRUE.equals(payload.get("requiresVerification")))
                .requiresPrescription(Boolean.TRUE.equals(payload.get("requiresPrescription")))
                .entities(entities)
                .nextAction((String) payload.getOrDefault("nextAction", "Please clarify the item or task."))
                .message((String) payload.getOrDefault("message", "Unable to identify your request."))
                .build();

        if (!intentResponseValidator.isValid(response)) {
            throw new IllegalArgumentException("AI response failed validation");
        }

        return response;
    }

    private Map<String, Object> extractPayload(Object content) {
        if (content instanceof Map<?, ?> map) {
            Map<String, Object> payload = new HashMap<>();
            map.forEach((key, value) -> payload.put(String.valueOf(key), value));
            return payload;
        }

        if (content instanceof String text) {
            try {
                return objectMapper.readValue(text, new TypeReference<Map<String, Object>>() {});
            } catch (Exception ex) {
                throw new IllegalArgumentException("AI response did not contain a valid JSON payload", ex);
            }
        }

        return objectMapper.convertValue(content, new TypeReference<Map<String, Object>>() {});
    }

    private IntentType parseIntent(String value) {
        try {
            return IntentType.valueOf(value.toUpperCase());
        } catch (Exception ex) {
            return IntentType.UNKNOWN;
        }
    }

    private PlanType parsePlan(String value) {
        try {
            return PlanType.valueOf(value.toUpperCase());
        } catch (Exception ex) {
            return PlanType.REJECTED;
        }
    }

    private double toDouble(Object value, double fallback) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value instanceof String text) {
            try {
                return Double.parseDouble(text);
            } catch (Exception ignored) {
                return fallback;
            }
        }
        return fallback;
    }
}
