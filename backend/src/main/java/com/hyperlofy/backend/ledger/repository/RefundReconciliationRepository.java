package com.hyperlofy.backend.ledger.repository;

import com.hyperlofy.backend.ledger.entity.RefundReconciliation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefundReconciliationRepository extends JpaRepository<RefundReconciliation, UUID> {
    Optional<RefundReconciliation> findByOrderId(UUID orderId);
    List<RefundReconciliation> findByStatus(String status);
}
