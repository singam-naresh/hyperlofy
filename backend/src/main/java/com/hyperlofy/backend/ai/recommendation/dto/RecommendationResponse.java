package com.hyperlofy.backend.ai.recommendation.dto;

import com.hyperlofy.backend.ai.recommendation.RecommendationReason;
import com.hyperlofy.backend.ai.recommendation.RecommendationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {

    private UUID recommendationId;
    private UUID customerId;
    private UUID conversationId;
    private UUID orderDraftId;
    private String recommendedItem;
    private RecommendationReason reason;
    private RecommendationType recommendationType;
    private double score;
    private boolean accepted;
    private boolean dismissed;
    private OffsetDateTime createdAt;
}
