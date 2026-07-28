package com.hyperlofy.backend.settlement.repository;

import com.hyperlofy.backend.settlement.entity.SettlementRiskEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SettlementRiskEventRepository extends JpaRepository<SettlementRiskEvent, UUID> {
    List<SettlementRiskEvent> findBySettlementId(UUID settlementId);
}
