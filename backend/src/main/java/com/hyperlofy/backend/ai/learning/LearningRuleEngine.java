package com.hyperlofy.backend.ai.learning;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;

@Component
public class LearningRuleEngine {

    public LearningScore evaluate(LearningRequest request) {
        double baseConfidence = request.getConfidence() != null ? request.getConfidence() : 0.5;
        double frequency = request.getFrequency() != null ? request.getFrequency() : 1.0;
        double recency = request.getRecency() != null ? request.getRecency() : 1.0;
        double decay = calculateDecay(request.getLearningType());
        double score = computeScore(request.getLearningType(), baseConfidence, frequency, recency, decay);

        return LearningScore.builder()
                .confidence(baseConfidence)
                .frequency(frequency)
                .recency(recency)
                .decay(decay)
                .weightedScore(score)
                .build();
    }

    private double calculateDecay(LearningType type) {
        if (type == LearningType.RECOMMENDATION_ACCEPTED || type == LearningType.MERCHANT_SELECTED) {
            return 0.85;
        }
        if (type == LearningType.RECOMMENDATION_REJECTED || type == LearningType.MERCHANT_REJECTED) {
            return 0.9;
        }
        return 0.95;
    }

    private double computeScore(LearningType type, double confidence, double frequency, double recency, double decay) {
        double typeMultiplier = switch (type) {
            case RECOMMENDATION_ACCEPTED, MERCHANT_SELECTED, ORDER_PLACED, VERIFICATION_PASSED -> 1.2;
            case RECOMMENDATION_REJECTED, MERCHANT_REJECTED, ORDER_CANCELLED, VERIFICATION_FAILED -> 0.8;
            case CUSTOMER_PREFERENCE_NEGATIVE, CUSTOMER_CORRECTION -> 0.7;
            default -> 1.0;
        };

        double weighted = confidence * 0.4 + frequency * 0.25 + recency * 0.2 + (1.0 - decay) * 0.15;
        return Math.max(0.0, Math.min(1.0, weighted * typeMultiplier));
    }
}
