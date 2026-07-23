package com.hyperlofy.backend.ai.recommendation.repository;

import com.hyperlofy.backend.ai.recommendation.RecommendationEntity;
import com.hyperlofy.backend.ai.recommendation.RecommendationType;
import com.hyperlofy.backend.ai.recommendation.RecommendationReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecommendationRepository extends JpaRepository<RecommendationEntity, UUID> {
    List<RecommendationEntity> findByCustomerIdAndDismissedFalse(UUID customerId);
    List<RecommendationEntity> findByCustomerIdAndAcceptedFalseAndDismissedFalseOrderByScoreDesc(UUID customerId);
    List<RecommendationEntity> findByRecommendationTypeAndRecommendationReason(RecommendationType type, RecommendationReason reason);
}
