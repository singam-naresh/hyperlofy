package com.hyperlofy.backend.ai.learning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningSummary {
    private long totalEvents;
    private long preferenceAdjustments;
    private long merchantAdjustments;
    private long recommendationAdjustments;
    private double averageConfidence;
    private double eventDecayRate;
    private List<PreferenceScoreDto> topPreferences;
    private List<MerchantScoreDto> topMerchants;
    private List<RecommendationScoreDto> topRecommendations;
}
