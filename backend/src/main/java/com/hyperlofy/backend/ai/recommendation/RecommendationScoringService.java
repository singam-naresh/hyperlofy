package com.hyperlofy.backend.ai.recommendation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class RecommendationScoringService {

    public double score(RecommendationCandidate candidate, RecommendationInput context) {
        if (candidate == null || context == null) {
            return 0.0;
        }

        double baseScore = 0.1;
        baseScore += clamp(candidate.getConversationRelevance() * 0.25);
        baseScore += clamp(candidate.getMemoryMatch() * 0.20);
        baseScore += clamp(candidate.getMerchantAvailability() * 0.20);
        baseScore += clamp(candidate.getPopularity() * 0.15);
        baseScore += clamp(candidate.getSeasonality() * 0.10);
        baseScore += clamp(candidate.getPriceOpportunity() * 0.10);

        if (hasPreviousAcceptance(context.getPreviousRecommendations(), candidate.getItem())) {
            baseScore += 0.10;
        }

        if (context.getDraft() != null && context.getDraft().getItems() != null) {
            baseScore += context.getDraft().getItems().stream()
                    .filter(item -> candidate.getItem() != null && item.getItemName() != null)
                    .anyMatch(item -> containsIgnoreCase(candidate.getItem(), item.getItemName())) ? 0.08 : 0.0;
        }

        double finalScore = clamp(baseScore);
        return finalScore;
    }

    private boolean hasPreviousAcceptance(List<RecommendationEntity> previousRecommendations, String item) {
        if (previousRecommendations == null || item == null) {
            return false;
        }
        return previousRecommendations.stream()
                .anyMatch(r -> r.isAccepted() && containsIgnoreCase(r.getRecommendedItem(), item));
    }

    private boolean containsIgnoreCase(String text, String search) {
        return text != null && search != null && text.toLowerCase().contains(search.toLowerCase());
    }

    private double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
