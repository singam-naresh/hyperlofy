package com.hyperlofy.backend.settlement.repository;

import com.hyperlofy.backend.settlement.entity.SettlementGovernance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SettlementGovernanceRepository extends JpaRepository<SettlementGovernance, UUID> {
    Optional<SettlementGovernance> findBySettlementId(UUID settlementId);
}
