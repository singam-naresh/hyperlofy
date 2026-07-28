package com.hyperlofy.backend.wallet.repository;

import com.hyperlofy.backend.wallet.entity.WalletLedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WalletLedgerEntryRepository extends JpaRepository<WalletLedgerEntry, UUID> {
    List<WalletLedgerEntry> findByWalletIdOrderByCreatedAtDesc(UUID walletId);
}
