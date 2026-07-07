package com.hyperlofy.backend.ledger.controller;

import com.hyperlofy.backend.ledger.entity.LedgerEntry;
import com.hyperlofy.backend.ledger.entity.SettlementBatch;
import com.hyperlofy.backend.ledger.repository.LedgerEntryRepository;
import com.hyperlofy.backend.ledger.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ledger")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class LedgerController {

    private final LedgerService ledgerService;
    private final LedgerEntryRepository ledgerEntryRepository;

    @GetMapping("/verify")
    public ResponseEntity<String> verifyLedger() {
        boolean balanceOk = ledgerService.verifyLedgerIntegrity();
        if (balanceOk) {
            return ResponseEntity.ok("SUCCEEDED: Double-entry ledger books are fully aligned.");
        } else {
            return ResponseEntity.status(500).body("FAILED: Ledger integrity discrepancy detected.");
        }
    }

    @GetMapping("/reconcile")
    public ResponseEntity<String> reconcileEscrows() {
        boolean reconciliationOk = ledgerService.reconcilePaymentsAndEscrows();
        if (reconciliationOk) {
            return ResponseEntity.ok("SUCCEEDED: Escrows and payment ledgers perfectly match.");
        } else {
            return ResponseEntity.status(500).body("FAILED: Reconciliation mismatch found.");
        }
    }

    @PostMapping("/settle")
    public ResponseEntity<SettlementBatch> triggerSettlement() {
        SettlementBatch batch = ledgerService.triggerSettlementBatch();
        return ResponseEntity.ok(batch);
    }

    @GetMapping("/entries")
    public ResponseEntity<List<LedgerEntry>> getLedgerEntries() {
        return ResponseEntity.ok(ledgerEntryRepository.findAll());
    }
}
