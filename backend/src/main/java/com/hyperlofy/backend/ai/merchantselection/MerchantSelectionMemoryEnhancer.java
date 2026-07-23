package com.hyperlofy.backend.ai.merchantselection;

import com.hyperlofy.backend.ai.memory.dto.MemoryDto;
import com.hyperlofy.backend.ai.memory.MemoryService;
import com.hyperlofy.backend.ai.orderbuilder.OrderDraft;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MerchantSelectionMemoryEnhancer {

    private final MemoryService memoryService;

    public void applyMemoryBias(MerchantSelectionRequest request) {
        if (request == null || request.getDraft() == null) {
            return;
        }

        List<MemoryDto> relevant = memoryService.findRelevantMemory(request.getDraft().getCustomerId(), request.getDraft());
        if (request.getCustomerPreferences() == null) {
            request.setCustomerPreferences(new java.util.HashMap<>());
        }
        request.getCustomerPreferences().put("memories", relevant);
    }

    public double boostMerchantScore(MerchantCandidate candidate, OrderDraft draft, List<MemoryDto> memories) {
        if (candidate == null || draft == null || memories == null || memories.isEmpty()) {
            return 0.0;
        }

        double boost = 0.0;
        for (MemoryDto memory : memories) {
            if (memory.getMemoryType().name().contains("BRAND") && candidate.getMerchantName().toLowerCase().contains(memory.getValue().toLowerCase())) {
                boost += memory.getConfidence() * 0.4;
            }
            if (memory.getMemoryType() == com.hyperlofy.backend.ai.memory.MemoryType.FAVORITE_CATEGORY || memory.getMemoryType() == com.hyperlofy.backend.ai.memory.MemoryType.FOOD_PREFERENCE) {
                if (candidate.getCapabilities().stream().anyMatch(cap -> cap.toLowerCase().contains(memory.getValue().toLowerCase()))) {
                    boost += memory.getConfidence() * 0.2;
                }
            }
        }
        return Math.min(boost, 0.5);
    }
}
