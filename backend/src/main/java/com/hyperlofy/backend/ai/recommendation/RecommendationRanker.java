package com.hyperlofy.backend.ai.recommendation;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecommendationRanker {

    public List<RecommendationEntity> rank(List<RecommendationEntity> recommendations) {
        if (recommendations == null || recommendations.isEmpty()) {
            return List.of();
        }
        return recommendations.stream()
                .sorted(Comparator.comparingDouble(RecommendationEntity::getScore).reversed())
                .collect(Collectors.toList());
    }
}
