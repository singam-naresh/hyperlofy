package com.hyperlofy.backend.ledger.repository;

import com.hyperlofy.backend.ledger.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {
    List<LedgerEntry> findByOrderId(UUID orderId);
    List<LedgerEntry> findByDebitAccountOrCreditAccount(String debitAccount, String creditAccount);
}
