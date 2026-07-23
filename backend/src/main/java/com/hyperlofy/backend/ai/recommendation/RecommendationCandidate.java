package com.hyperlofy.backend.ai.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationCandidate {
    private String item;
    private RecommendationType type;
    private RecommendationReason reason;
    private double popularity;
    private double seasonality;
    private double memoryMatch;
    private double merchantAvailability;
    private double conversationRelevance;
    private double priceOpportunity;
    private double score;
}
