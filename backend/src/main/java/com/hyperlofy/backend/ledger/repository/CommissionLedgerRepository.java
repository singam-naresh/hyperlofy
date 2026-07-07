package com.hyperlofy.backend.ledger.repository;

import com.hyperlofy.backend.ledger.entity.CommissionLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommissionLedgerRepository extends JpaRepository<CommissionLedger, UUID> {
    Optional<CommissionLedger> findByOrderId(UUID orderId);
    List<CommissionLedger> findByAgentId(UUID agentId);
}
