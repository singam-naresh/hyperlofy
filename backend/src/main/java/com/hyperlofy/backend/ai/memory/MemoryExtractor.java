package com.hyperlofy.backend.ai.memory;

import com.hyperlofy.backend.ai.memory.dto.MemoryCreateRequest;
import com.hyperlofy.backend.ai.conversation.ConversationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class MemoryExtractor {

    public List<MemoryCreateRequest> extractMemoriesFromContext(ConversationContext conversation) {
        if (conversation == null || conversation.getCollectedEntities() == null) {
            return List.of();
        }

        List<MemoryCreateRequest> memories = new ArrayList<>();
        Map<String, Object> entities = conversation.getCollectedEntities();

        if (containsOrganicPreference(conversation)) {
            memories.add(MemoryCreateRequest.builder()
                    .memoryType(MemoryType.FOOD_PREFERENCE)
                    .key("organic_preference")
                    .value("organic vegetables")
                    .confidence(0.85)
                    .build());
        }

        if (entityPresent(entities, "brand")) {
            memories.add(MemoryCreateRequest.builder()
                    .memoryType(MemoryType.BRAND)
                    .key("preferred_brand")
                    .value(String.valueOf(entities.get("brand")))
                    .confidence(0.9)
                    .build());
        }

        if (entityPresent(entities, "shopping_day") || entityPresent(entities, "shopping_pattern")) {
            String value = String.valueOf(entities.getOrDefault("shopping_day", entities.get("shopping_pattern")));
            memories.add(MemoryCreateRequest.builder()
                    .memoryType(MemoryType.SHOPPING_PATTERN)
                    .key("shopping_day")
                    .value(value)
                    .confidence(0.8)
                    .build());
        }

        if (entityPresent(entities, "delivery_time") || containsLaterDeliveryPreference(conversation)) {
            memories.add(MemoryCreateRequest.builder()
                    .memoryType(MemoryType.DELIVERY_PREFERENCE)
                    .key("preferred_delivery_time")
                    .value(String.valueOf(entities.getOrDefault("delivery_time", "after 8 PM")))
                    .confidence(0.75)
                    .build());
        }

        if (entityPresent(entities, "allergy")) {
            memories.add(MemoryCreateRequest.builder()
                    .memoryType(MemoryType.ALLERGY)
                    .key("allergy")
                    .value(String.valueOf(entities.get("allergy")))
                    .confidence(0.95)
                    .build());
        }

        if (entityPresent(entities, "delivery_address") || entityPresent(entities, "drop")) {
            memories.add(MemoryCreateRequest.builder()
                    .memoryType(MemoryType.ADDRESS_PREFERENCE)
                    .key("preferred_address")
                    .value(String.valueOf(entities.getOrDefault("delivery_address", entities.get("drop"))))
                    .confidence(0.8)
                    .build());
        }

        return memories;
    }

    private boolean containsOrganicPreference(ConversationContext conversation) {
        String text = conversation.getLastQuestion() == null ? "" : conversation.getLastQuestion().toLowerCase();
        return text.contains("organic") || containsEntityValue(conversation, "organic");
    }

    private boolean containsLaterDeliveryPreference(ConversationContext conversation) {
        String text = conversation.getLastQuestion() == null ? "" : conversation.getLastQuestion().toLowerCase();
        return text.contains("after 8 pm") || text.contains("after 8pm") || text.contains("evening delivery");
    }

    private boolean containsEntityValue(ConversationContext conversation, String term) {
        if (conversation.getCollectedEntities() == null) {
            return false;
        }
        return conversation.getCollectedEntities().values().stream()
                .filter(value -> value != null)
                .map(Object::toString)
                .map(String::toLowerCase)
                .anyMatch(v -> v.contains(term));
    }

    private boolean entityPresent(Map<String, Object> entities, String key) {
        return entities.containsKey(key) && entities.get(key) != null && !String.valueOf(entities.get(key)).isBlank();
    }
}
