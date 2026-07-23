package com.hyperlofy.backend.ai.recommendation;

import com.hyperlofy.backend.ai.recommendation.dto.RecommendationResponse;
import org.springframework.stereotype.Component;

@Component
public class RecommendationMapper {

    public RecommendationResponse toDto(RecommendationEntity entity) {
        if (entity == null) {
            return null;
        }

        return RecommendationResponse.builder()
                .recommendationId(entity.getRecommendationId())
                .customerId(entity.getCustomerId())
                .conversationId(entity.getConversationId())
                .orderDraftId(entity.getOrderDraftId())
                .recommendedItem(entity.getRecommendedItem())
                .reason(entity.getReason())
                .recommendationType(entity.getRecommendationType())
                .score(entity.getScore())
                .accepted(entity.isAccepted())
                .dismissed(entity.isDismissed())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
