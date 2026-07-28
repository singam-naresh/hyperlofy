package com.hyperlofy.backend.settlement.controller;

import com.hyperlofy.backend.settlement.entity.Settlement;
import com.hyperlofy.backend.settlement.entity.SettlementPayout;
import com.hyperlofy.backend.settlement.service.SettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/settlements/admin")
@RequiredArgsConstructor
@Tag(name = "Settlement Engine Admin API", description = "Endpoints for financial operations to oversee merchant/driver settlements and manually approve payouts")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class SettlementAdminController {

    private final SettlementService settlementService;

    @GetMapping("/{id}")
    @Operation(summary = "Admin Inspect Settlement", description = "Returns full settlement breakdown, commission rates, and tax amounts.")
    public ResponseEntity<Settlement> inspectSettlement(@PathVariable UUID id) {
        return ResponseEntity.ok(settlementService.getSettlement(id));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Admin Approve Bank Payout", description = "Triggers manual approval and bank payout dispatch for a settlement.")
    public ResponseEntity<SettlementPayout> approvePayout(@PathVariable UUID id) {
        return ResponseEntity.ok(settlementService.processPayout(id));
    }
}
