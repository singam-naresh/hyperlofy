package com.hyperlofy.backend.ai.learning;

import org.springframework.stereotype.Component;

@Component
public class LearningMapper {

    public LearningResponse toDto(LearningEntity entity) {
        if (entity == null) {
            return null;
        }

        return LearningResponse.builder()
                .learningId(entity.getLearningId())
                .customerId(entity.getCustomer() != null ? entity.getCustomer().getId() : null)
                .learningType(entity.getLearningType())
                .score(entity.getScore())
                .confidence(entity.getConfidence())
                .recency(entity.getRecency())
                .frequency(entity.getFrequency())
                .details(entity.getDetails())
                .build();
    }
}
