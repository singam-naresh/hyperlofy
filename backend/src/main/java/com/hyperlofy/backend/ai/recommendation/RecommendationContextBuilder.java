package com.hyperlofy.backend.ai.recommendation;

import com.hyperlofy.backend.ai.conversation.ConversationResponse;
import com.hyperlofy.backend.ai.merchantselection.MerchantCandidate;
import com.hyperlofy.backend.ai.memory.dto.MemoryDto;
import com.hyperlofy.backend.ai.orderbuilder.OrderDraft;
import com.hyperlofy.backend.ai.recommendation.dto.RecommendationRequest;
import com.hyperlofy.backend.ai.recommendation.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationContextBuilder {

    private final RecommendationRepository recommendationRepository;

    public RecommendationInput buildContext(RecommendationRequest request,
                                            ConversationResponse conversation,
                                            List<MemoryDto> memories,
                                            List<MerchantCandidate> merchantCandidates,
                                            OrderDraft draft) {

        RecommendationInput input = new RecommendationInput();
        input.setCustomerId(request.getCustomerId());
        input.setConversationId(request.getConversationId());
        input.setOrderDraftId(request.getOrderDraftId());
        input.setPrompt(request.getPrompt());
        input.setRecommendationType(request.getScenario());
        input.setConversation(conversation);
        input.setMemories(memories);
        input.setDraft(draft);
        input.setMerchantCandidates(merchantCandidates);
        input.setPreviousRecommendations(recommendationRepository.findByCustomerIdAndAcceptedFalseAndDismissedFalseOrderByScoreDesc(request.getCustomerId()));

        return input;
    }
}
