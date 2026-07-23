package com.hyperlofy.backend.ai.learning;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LearningAnalyzer {

    private final LearningRepository learningRepository;

    public List<PreferenceScoreDto> analyzePreferences(UUID customerId) {
        return learningRepository.findByCustomer_IdOrderByEventAtDesc(customerId).stream()
                .filter(event -> event.getLearningType() == LearningType.CUSTOMER_PREFERENCE_NEGATIVE)
                .map(event -> PreferenceScoreDto.builder()
                        .preferenceKey(event.getDetails())
                        .score(event.getScore())
                        .confidence(event.getConfidence())
                        .recency(event.getRecency())
                        .frequency(event.getFrequency())
                        .build())
                .collect(Collectors.toList());
    }

    public List<MerchantScoreDto> analyzeMerchantFeedback(UUID merchantId) {
        return learningRepository.findByMerchantIdOrderByEventAtDesc(merchantId).stream()
                .map(event -> MerchantScoreDto.builder()
                        .merchantId(event.getMerchantId())
                        .score(event.getScore())
                        .confidence(event.getConfidence())
                        .recency(event.getRecency())
                        .frequency(event.getFrequency())
                        .build())
                .collect(Collectors.toList());
    }

    public List<RecommendationScoreDto> analyzeRecommendationFeedback(UUID customerId) {
        return learningRepository.findByCustomer_IdOrderByEventAtDesc(customerId).stream()
                .filter(event -> event.getLearningType() == LearningType.RECOMMENDATION_ACCEPTED
                        || event.getLearningType() == LearningType.RECOMMENDATION_REJECTED)
                .map(event -> RecommendationScoreDto.builder()
                        .recommendationId(event.getRecommendationId())
                        .score(event.getScore())
                        .confidence(event.getConfidence())
                        .recency(event.getRecency())
                        .frequency(event.getFrequency())
                        .build())
                .collect(Collectors.toList());
    }
}
