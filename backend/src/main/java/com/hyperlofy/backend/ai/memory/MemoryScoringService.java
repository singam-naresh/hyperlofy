package com.hyperlofy.backend.ai.memory;

import com.hyperlofy.backend.ai.memory.dto.MemoryDto;
import com.hyperlofy.backend.ai.orderbuilder.OrderDraft;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MemoryScoringService {

    public double scoreMemory(MemoryDto memory, OrderDraft draft) {
        if (memory == null || draft == null) {
            return 0.0;
        }

        switch (memory.getMemoryType()) {
            case FAVORITE_CATEGORY:
            case FOOD_PREFERENCE:
            case BRAND:
            case ALLERGY:
                return scoreCatalogPreference(memory, draft);
            case SHOPPING_PATTERN:
            case DELIVERY_PREFERENCE:
            case ADDRESS_PREFERENCE:
            case PAYMENT_PREFERENCE:
            case TIME_PREFERENCE:
            case CUSTOM_NOTE:
            default:
                return scoreContextualPreference(memory, draft);
        }
    }

    private double scoreCatalogPreference(MemoryDto memory, OrderDraft draft) {
        if (draft.getItems() == null || draft.getItems().isEmpty()) {
            return 0.0;
        }
        String normalizedValue = memory.getValue().trim().toLowerCase();
        long matches = draft.getItems().stream()
                .map(item -> item.getItemName() == null ? "" : item.getItemName().toLowerCase())
                .filter(itemName -> itemName.contains(normalizedValue) || normalizedValue.contains(itemName))
                .count();
        return Math.min(1.0, matches * 0.35 + memory.getConfidence() * 0.3);
    }

    private double scoreContextualPreference(MemoryDto memory, OrderDraft draft) {
        if (draft.getMetadata() == null) {
            return memory.getConfidence() * 0.25;
        }
        String normalizedValue = memory.getValue().trim().toLowerCase();
        Map<String, Object> metadata = Map.of(
                "plan", draft.getPlan(),
                "intent", draft.getIntent(),
                "type", draft.getOrderType()
        );
        boolean related = metadata.values().stream()
                .filter(v -> v != null)
                .map(Object::toString)
                .map(String::toLowerCase)
                .anyMatch(v -> v.contains(normalizedValue) || normalizedValue.contains(v));
        return related ? Math.min(1.0, memory.getConfidence() * 0.4 + 0.3) : memory.getConfidence() * 0.2;
    }
}
