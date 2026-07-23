package com.hyperlofy.backend.ai.learning;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearningStatisticsService {

    private final LearningRepository learningRepository;

    @Transactional(readOnly = true)
    public LearningSummary calculateSummary(java.util.UUID customerId) {
        List<LearningEntity> events = learningRepository.findByCustomer_IdOrderByEventAtDesc(customerId);
        long total = events.size();
        long preferenceAdjustments = events.stream()
                .filter(event -> event.getLearningType() == LearningType.CUSTOMER_PREFERENCE_NEGATIVE)
                .count();
        long merchantAdjustments = events.stream()
                .filter(event -> event.getLearningType() == LearningType.MERCHANT_SELECTED
                        || event.getLearningType() == LearningType.MERCHANT_REJECTED)
                .count();
        long recommendationAdjustments = events.stream()
                .filter(event -> event.getLearningType() == LearningType.RECOMMENDATION_ACCEPTED
                        || event.getLearningType() == LearningType.RECOMMENDATION_REJECTED)
                .count();
        double averageConfidence = events.stream()
                .mapToDouble(LearningEntity::getConfidence)
                .average()
                .orElse(0.0);
        double averageDecay = events.stream()
                .mapToDouble(event -> 1.0 - event.getScore())
                .average()
                .orElse(0.0);

        List<PreferenceScoreDto> topPreferences = events.stream()
                .filter(event -> event.getLearningType() == LearningType.CUSTOMER_PREFERENCE_NEGATIVE)
                .map(event -> PreferenceScoreDto.builder()
                        .preferenceKey(event.getDetails())
                        .score(event.getScore())
                        .confidence(event.getConfidence())
                        .recency(event.getRecency())
                        .frequency(event.getFrequency())
                        .build())
                .limit(5)
                .collect(Collectors.toList());

        List<MerchantScoreDto> topMerchants = events.stream()
                .filter(event -> event.getMerchantId() != null)
                .map(event -> MerchantScoreDto.builder()
                        .merchantId(event.getMerchantId())
                        .score(event.getScore())
                        .confidence(event.getConfidence())
                        .recency(event.getRecency())
                        .frequency(event.getFrequency())
                        .build())
                .limit(5)
                .collect(Collectors.toList());

        List<RecommendationScoreDto> topRecommendations = events.stream()
                .filter(event -> event.getRecommendationId() != null)
                .map(event -> RecommendationScoreDto.builder()
                        .recommendationId(event.getRecommendationId())
                        .score(event.getScore())
                        .confidence(event.getConfidence())
                        .recency(event.getRecency())
                        .frequency(event.getFrequency())
                        .build())
                .limit(5)
                .collect(Collectors.toList());

        return LearningSummary.builder()
                .totalEvents(total)
                .preferenceAdjustments(preferenceAdjustments)
                .merchantAdjustments(merchantAdjustments)
                .recommendationAdjustments(recommendationAdjustments)
                .averageConfidence(averageConfidence)
                .eventDecayRate(averageDecay)
                .topPreferences(topPreferences)
                .topMerchants(topMerchants)
                .topRecommendations(topRecommendations)
                .build();
    }
}
