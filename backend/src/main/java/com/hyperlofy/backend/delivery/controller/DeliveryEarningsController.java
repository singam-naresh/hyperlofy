package com.hyperlofy.backend.delivery.controller;

import com.hyperlofy.backend.agent.entity.WithdrawalRequest;
import com.hyperlofy.backend.delivery.dto.DeliveryEarningsDTO;
import com.hyperlofy.backend.delivery.service.DeliveryPlatformService;
import com.hyperlofy.backend.ledger.entity.CommissionLedger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
@Tag(name = "Delivery Partner Earnings & Settlements API", description = "Endpoints for delivery partner wallet balance, earnings history, and payout audit")
@PreAuthorize("hasAnyRole('AGENT', 'ADMIN', 'SUPER_ADMIN')")
public class DeliveryEarningsController {

    private final DeliveryPlatformService deliveryPlatformService;

    @GetMapping("/earnings")
    @Operation(summary = "Get Earnings Overview", description = "Retrieves complete earnings overview including today's, weekly, monthly, lifetime earnings, wallet balance, and payout history.")
    public ResponseEntity<DeliveryEarningsDTO> getEarnings(@RequestParam UUID agentId) {
        return ResponseEntity.ok(deliveryPlatformService.getEarningsOverview(agentId));
    }

    @GetMapping("/ledger")
    @Operation(summary = "Get Commission Ledger Audit", description = "Retrieves immutable commission ledger entries for completed delivery fee payouts.")
    public ResponseEntity<List<CommissionLedger>> getLedger(@RequestParam UUID agentId) {
        DeliveryEarningsDTO overview = deliveryPlatformService.getEarningsOverview(agentId);
        return ResponseEntity.ok(overview.getLedgerHistory());
    }

    @GetMapping("/settlements")
    @Operation(summary = "Get Settlement History", description = "Retrieves delivery partner settlement records.")
    public ResponseEntity<DeliveryEarningsDTO> getSettlements(@RequestParam UUID agentId) {
        return ResponseEntity.ok(deliveryPlatformService.getEarningsOverview(agentId));
    }

    @GetMapping("/payouts")
    @Operation(summary = "Get Payout History", description = "Retrieves withdrawal and bank payout transaction history for partner.")
    public ResponseEntity<List<WithdrawalRequest>> getPayouts(@RequestParam UUID agentId) {
        DeliveryEarningsDTO overview = deliveryPlatformService.getEarningsOverview(agentId);
        return ResponseEntity.ok(overview.getPayoutHistory());
    }
}
