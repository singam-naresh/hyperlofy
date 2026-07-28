package com.hyperlofy.backend.analytics.repository;

import com.hyperlofy.backend.analytics.entity.AnalyticsPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnalyticsPredictionRepository extends JpaRepository<AnalyticsPrediction, UUID> {
    List<AnalyticsPrediction> findByPredictionTargetOrderByCreatedAtDesc(String predictionTarget);
}
