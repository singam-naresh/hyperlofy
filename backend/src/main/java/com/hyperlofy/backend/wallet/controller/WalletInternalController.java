package com.hyperlofy.backend.wallet.controller;

import com.hyperlofy.backend.wallet.entity.Wallet;
import com.hyperlofy.backend.wallet.entity.WalletHold;
import com.hyperlofy.backend.wallet.service.WalletEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallet/internal")
@RequiredArgsConstructor
@Tag(name = "Wallet Engine Internal Integration API", description = "Endpoints for Unified Order Engine & Payments Engine to credit, debit, or lock escrow holds on user wallets")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class WalletInternalController {

    private final WalletEngineService walletService;

    @PostMapping("/credit")
    @Operation(summary = "Credit User Wallet", description = "Atomically adds funds to user wallet balance and appends ledger entry.")
    public ResponseEntity<Wallet> credit(
            @RequestParam UUID ownerId,
            @RequestParam BigDecimal amount,
            @RequestParam String description,
            @RequestParam(required = false) UUID referenceId) {
        return ResponseEntity.ok(walletService.creditWallet(ownerId, amount, description, referenceId));
    }

    @PostMapping("/debit")
    @Operation(summary = "Debit User Wallet", description = "Deducts funds from user spendable balance for order payment.")
    public ResponseEntity<Wallet> debit(
            @RequestParam UUID ownerId,
            @RequestParam BigDecimal amount,
            @RequestParam String description,
            @RequestParam(required = false) UUID referenceId) {
        return ResponseEntity.ok(walletService.debitWallet(ownerId, amount, description, referenceId));
    }

    @PostMapping("/hold")
    @Operation(summary = "Lock Escrow Hold", description = "Transfers funds from spendable balance to reserved balance for active order escrow.")
    public ResponseEntity<WalletHold> placeHold(
            @RequestParam UUID ownerId,
            @RequestParam UUID orderId,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(walletService.placeEscrowHold(ownerId, orderId, amount));
    }
}
