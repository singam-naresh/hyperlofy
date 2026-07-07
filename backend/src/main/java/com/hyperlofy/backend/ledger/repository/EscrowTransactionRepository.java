package com.hyperlofy.backend.ledger.repository;

import com.hyperlofy.backend.ledger.entity.EscrowTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EscrowTransactionRepository extends JpaRepository<EscrowTransaction, UUID> {
    Optional<EscrowTransaction> findByOrderId(UUID orderId);
}
