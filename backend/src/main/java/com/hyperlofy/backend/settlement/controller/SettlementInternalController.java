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

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/settlements/internal")
@RequiredArgsConstructor
@Tag(name = "Settlement Engine Internal Integration API", description = "Endpoints for Unified Order Engine & Payments Engine to create net settlements and trigger automated bank payouts")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class SettlementInternalController {

    private final SettlementService settlementService;

    @PostMapping("/create")
    @Operation(summary = "Create Order Settlement Record", description = "Calculates platform commission, GST taxes, and net payout amount for merchant/driver.")
    public ResponseEntity<Settlement> createSettlement(
            @RequestParam UUID orderId,
            @RequestParam UUID payeeId,
            @RequestParam String payeeType,
            @RequestParam BigDecimal grossAmount,
            @RequestParam(required = false) BigDecimal commissionPercent,
            @RequestParam(required = false) BigDecimal taxPercent) {
        return ResponseEntity.ok(settlementService.createSettlement(orderId, payeeId, payeeType, grossAmount, commissionPercent, taxPercent));
    }

    @PostMapping("/process")
    @Operation(summary = "Process Bank Payout", description = "Executes automated bank payout to verified beneficiary account.")
    public ResponseEntity<SettlementPayout> processPayout(@RequestParam UUID settlementId) {
        return ResponseEntity.ok(settlementService.processPayout(settlementId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Settlement Details", description = "Returns itemized settlement calculation breakdown and payout reference.")
    public ResponseEntity<Settlement> getSettlement(@PathVariable UUID id) {
        return ResponseEntity.ok(settlementService.getSettlement(id));
    }
}
