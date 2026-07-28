package com.hyperlofy.backend.wallet.controller;

import com.hyperlofy.backend.wallet.entity.Wallet;
import com.hyperlofy.backend.wallet.service.WalletEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallet/enterprise")
@RequiredArgsConstructor
@Tag(name = "Wallet Engine Enterprise Addendum API", description = "Endpoints for Master Treasury transfers, spending policy enforcement, multi-currency readiness, and financial governance")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class WalletEnterpriseController {

    private final WalletEnterpriseService enterpriseService;

    @PostMapping("/treasury/transfer")
    @Operation(summary = "Master Treasury Transfer", description = "Executes dual-approval internal transfers between master treasury, operational, and reserve wallets.")
    public ResponseEntity<Wallet> treasuryTransfer(
            @RequestParam UUID sourceOwnerId,
            @RequestParam UUID targetOwnerId,
            @RequestParam BigDecimal amount,
            @RequestParam String purpose) {
        return ResponseEntity.ok(enterpriseService.transferTreasuryFunds(sourceOwnerId, targetOwnerId, amount, purpose));
    }
}
