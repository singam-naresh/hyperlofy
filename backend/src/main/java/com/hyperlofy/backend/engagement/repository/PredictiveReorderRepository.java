package com.hyperlofy.backend.engagement.repository;

import com.hyperlofy.backend.engagement.entity.PredictiveReorder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PredictiveReorderRepository extends JpaRepository<PredictiveReorder, UUID> {
    Optional<PredictiveReorder> findByPredictionCode(String predictionCode);
    List<PredictiveReorder> findByCustomerId(UUID customerId);
}
