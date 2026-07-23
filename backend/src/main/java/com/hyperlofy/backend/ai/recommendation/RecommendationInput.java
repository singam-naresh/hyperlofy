package com.hyperlofy.backend.ai.recommendation;

import com.hyperlofy.backend.ai.conversation.ConversationResponse;
import com.hyperlofy.backend.ai.merchantselection.MerchantCandidate;
import com.hyperlofy.backend.ai.memory.dto.MemoryDto;
import com.hyperlofy.backend.ai.orderbuilder.OrderDraft;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationInput {
    private UUID customerId;
    private UUID conversationId;
    private UUID orderDraftId;
    private String prompt;
    private RecommendationType recommendationType;
    private ConversationResponse conversation;
    private List<MemoryDto> memories;
    private OrderDraft draft;
    private List<MerchantCandidate> merchantCandidates;
    private List<RecommendationEntity> previousRecommendations;
}
