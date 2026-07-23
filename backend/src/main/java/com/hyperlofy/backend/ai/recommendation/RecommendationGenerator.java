package com.hyperlofy.backend.ai.recommendation;

import com.hyperlofy.backend.ai.memory.dto.MemoryDto;
import com.hyperlofy.backend.ai.orderbuilder.OrderDraft;
import com.hyperlofy.backend.ai.recommendation.dto.RecommendationRequest;
import com.hyperlofy.backend.ai.recommendation.dto.RecommendationResponse;
import com.hyperlofy.backend.ai.recommendation.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RecommendationGenerator {

    private final RecommendationRepository recommendationRepository;
    private final RecommendationScoringService scoringService;

    public List<RecommendationEntity> generateRecommendations(RecommendationInput input) {
        if (input == null) {
            return Collections.emptyList();
        }

        List<RecommendationCandidate> candidates = buildCandidates(input);
        List<RecommendationEntity> entities = new ArrayList<>();

        for (RecommendationCandidate candidate : candidates) {
            double score = scoringService.score(candidate, input);
            candidate.setScore(score);

            RecommendationEntity entity = RecommendationEntity.builder()
                    .recommendationId(UUID.randomUUID())
                    .customerId(input.getCustomerId())
                    .conversationId(input.getConversationId())
                    .orderDraftId(input.getOrderDraftId())
                    .recommendedItem(candidate.getItem())
                    .recommendationType(candidate.getType())
                    .reason(candidate.getReason())
                    .score(score)
                    .accepted(false)
                    .dismissed(false)
                    .build();
            entities.add(entity);
        }

        entities.sort(Comparator.comparingDouble(RecommendationEntity::getScore).reversed());
        return entities;
    }

    private List<RecommendationCandidate> buildCandidates(RecommendationInput input) {
        List<RecommendationCandidate> candidates = new ArrayList<>();

        if (input.getDraft() != null && input.getDraft().getItems() != null) {
            for (var item : input.getDraft().getItems()) {
                candidates.add(RecommendationCandidate.builder()
                        .item(item.getItemName())
                        .type(RecommendationType.COMPLEMENTARY_PRODUCT)
                        .reason(RecommendationReason.CONTEXTUAL_RELEVANCE)
                        .conversationRelevance(0.3)
                        .memoryMatch(0.2)
                        .merchantAvailability(0.5)
                        .popularity(0.4)
                        .seasonality(0.2)
                        .priceOpportunity(0.1)
                        .build());
            }
        }

        if (input.getMemories() != null) {
            for (MemoryDto memory : input.getMemories()) {
                candidates.add(RecommendationCandidate.builder()
                        .item(memory.getValue())
                        .type(RecommendationType.MEMORY_BASED)
                        .reason(RecommendationReason.MEMORY_MATCH)
                        .conversationRelevance(0.1)
                        .memoryMatch(memory.getConfidence())
                        .merchantAvailability(0.3)
                        .popularity(0.2)
                        .seasonality(0.1)
                        .priceOpportunity(0.05)
                        .build());
            }
        }

        candidates.add(RecommendationCandidate.builder()
                .item("Bread")
                .type(RecommendationType.FREQUENT_PURCHASE)
                .reason(RecommendationReason.FREQUENCY)
                .conversationRelevance(0.1)
                .memoryMatch(0.1)
                .merchantAvailability(0.8)
                .popularity(0.6)
                .seasonality(0.2)
                .priceOpportunity(0.05)
                .build());

        candidates.add(RecommendationCandidate.builder()
                .item("Mint")
                .type(RecommendationType.MISSING_INGREDIENT)
                .reason(RecommendationReason.CONTEXTUAL_RELEVANCE)
                .conversationRelevance(0.7)
                .memoryMatch(0.0)
                .merchantAvailability(0.6)
                .popularity(0.5)
                .seasonality(0.4)
                .priceOpportunity(0.1)
                .build());

        return candidates;
    }
}
