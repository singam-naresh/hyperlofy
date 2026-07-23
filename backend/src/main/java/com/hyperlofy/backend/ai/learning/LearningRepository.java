package com.hyperlofy.backend.ai.learning;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface LearningRepository extends JpaRepository<LearningEntity, UUID> {
    List<LearningEntity> findByCustomer_IdOrderByEventAtDesc(UUID customerId);
    List<LearningEntity> findByMerchantIdOrderByEventAtDesc(UUID merchantId);
    List<LearningEntity> findByLearningTypeAndEventAtAfter(LearningType type, OffsetDateTime after);
}
