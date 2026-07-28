package com.hyperlofy.backend.settlement.repository;

import com.hyperlofy.backend.settlement.entity.SettlementPayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SettlementPayoutRepository extends JpaRepository<SettlementPayout, UUID> {
    Optional<SettlementPayout> findBySettlementId(UUID settlementId);
}
