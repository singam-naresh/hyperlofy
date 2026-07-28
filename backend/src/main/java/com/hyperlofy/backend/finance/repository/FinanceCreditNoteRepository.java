package com.hyperlofy.backend.finance.repository;

import com.hyperlofy.backend.finance.entity.FinanceCreditNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FinanceCreditNoteRepository extends JpaRepository<FinanceCreditNote, UUID> {
    Optional<FinanceCreditNote> findByInvoiceId(UUID invoiceId);
}
