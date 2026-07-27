package com.hyperlofy.backend.ai.forecasting.repository;

import com.hyperlofy.backend.ai.forecasting.entity.MerchantIntelligenceSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MerchantIntelligenceSnapshotRepository extends JpaRepository<MerchantIntelligenceSnapshot, UUID> {
    Optional<MerchantIntelligenceSnapshot> findByMerchantId(UUID merchantId);
}
