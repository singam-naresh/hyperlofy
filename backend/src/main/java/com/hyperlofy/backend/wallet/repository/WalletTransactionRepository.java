package com.hyperlofy.backend.wallet.repository;

import com.hyperlofy.backend.wallet.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, UUID> {
    List<WalletTransaction> findByWalletIdOrderByCreatedAtDesc(UUID walletId);
    List<WalletTransaction> findByReferenceId(UUID referenceId);
}
