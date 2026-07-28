package com.hyperlofy.backend.wallet.controller;

import com.hyperlofy.backend.wallet.entity.Wallet;
import com.hyperlofy.backend.wallet.service.WalletEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallet/admin")
@RequiredArgsConstructor
@Tag(name = "Wallet Engine Admin API", description = "Endpoints for platform administrators to inspect user wallets and ledger entries")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class WalletAdminController {

    private final WalletEngineService walletService;

    @GetMapping("/{ownerId}")
    @Operation(summary = "Admin Inspect Wallet", description = "Returns full wallet balances, KYC status, and active holds.")
    public ResponseEntity<Wallet> inspectWallet(@PathVariable UUID ownerId) {
        return ResponseEntity.ok(walletService.getWalletByOwner(ownerId));
    }
}
