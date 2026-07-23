package com.hyperlofy.backend.ai.planner;

import com.hyperlofy.backend.ai.conversation.ConversationResponse;
import com.hyperlofy.backend.ai.merchantselection.MerchantSelectionResponse;
import com.hyperlofy.backend.ai.orderbuilder.OrderBuilderResponse;
import com.hyperlofy.backend.ai.recommendation.dto.RecommendationResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanningResponse {
    private boolean success;
    private PlanningStatus status;
    private String message;
    private ConversationResponse conversation;
    private OrderBuilderResponse orderDraft;
    private MerchantSelectionResponse merchantSelection;
    private RecommendationResponse recommendation;
}
