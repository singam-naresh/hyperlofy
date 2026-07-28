package com.hyperlofy.backend.wallet.controller;

import com.hyperlofy.backend.wallet.entity.Wallet;
import com.hyperlofy.backend.wallet.entity.WalletLedgerEntry;
import com.hyperlofy.backend.wallet.service.WalletEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallet/customer")
@RequiredArgsConstructor
@Tag(name = "Wallet Engine Customer API", description = "Endpoints for customers to view spendable/reserved wallet balances and transaction ledger history")
@PreAuthorize("hasRole('CUSTOMER')")
public class WalletCustomerController {

    private final WalletEngineService walletService;

    @GetMapping("/balance/{ownerId}")
    @Operation(summary = "Get Customer Wallet Balance", description = "Returns spendable, reserved, and promotional balance totals.")
    public ResponseEntity<Wallet> getBalance(@PathVariable UUID ownerId) {
        return ResponseEntity.ok(walletService.getWalletByOwner(ownerId));
    }

    @GetMapping("/ledger/{ownerId}")
    @Operation(summary = "Get Wallet Ledger History", description = "Returns chronological double-entry audit history of credits and debits.")
    public ResponseEntity<List<WalletLedgerEntry>> getLedger(@PathVariable UUID ownerId) {
        return ResponseEntity.ok(walletService.getLedgerEntries(ownerId));
    }
}
