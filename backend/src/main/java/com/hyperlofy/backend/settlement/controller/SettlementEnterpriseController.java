package com.hyperlofy.backend.settlement.controller;

import com.hyperlofy.backend.settlement.entity.SettlementBankRoute;
import com.hyperlofy.backend.settlement.entity.SettlementGovernance;
import com.hyperlofy.backend.settlement.entity.SettlementRiskEvent;
import com.hyperlofy.backend.settlement.entity.SettlementTreasury;
import com.hyperlofy.backend.settlement.service.SettlementEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/settlements/enterprise")
@RequiredArgsConstructor
@Tag(name = "Settlement Engine Enterprise Addendum API", description = "Endpoints for Intelligent Payout Routing, Treasury Operations, Risk Scoring, Dual-Approval Governance, and GST/TDS Compliance")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class SettlementEnterpriseController {

    private final SettlementEnterpriseService enterpriseService;

    @GetMapping("/treasury/position")
    @Operation(summary = "Get Treasury Liquidity Position", description = "Returns working capital liquidity reserves and locked escrow totals.")
    public ResponseEntity<SettlementTreasury> getTreasuryPosition(@RequestParam(defaultValue = "MAIN_OPERATIONAL_RESERVE") String reservePoolName) {
        return ResponseEntity.ok(enterpriseService.getTreasuryPosition(reservePoolName));
    }

    @GetMapping("/routes/optimal")
    @Operation(summary = "Select Optimal Payout Bank Route", description = "Dynamically routes bank payout based on success rate, latency, and operational health.")
    public ResponseEntity<SettlementBankRoute> getOptimalRoute() {
        return ResponseEntity.ok(enterpriseService.selectOptimalPayoutRoute());
    }

    @PostMapping("/risk-check")
    @Operation(summary = "Evaluate Payout Risk Score", description = "Performs automated risk assessment for duplicate payouts, abnormal velocity, or high-value transfers.")
    public ResponseEntity<SettlementRiskEvent> checkRisk(@RequestParam UUID settlementId, @RequestParam BigDecimal netAmount) {
        return ResponseEntity.ok(enterpriseService.evaluatePayoutRisk(settlementId, netAmount));
    }

    @PostMapping("/governance/request")
    @Operation(summary = "Submit Financial Governance Dual-Approval Request", description = "Triggers dual-approval workflow for high-value merchant payouts.")
    public ResponseEntity<SettlementGovernance> requestGovernance(@RequestParam UUID settlementId, @RequestParam String requestedBy, @RequestParam String notes) {
        return ResponseEntity.ok(enterpriseService.requestGovernanceApproval(settlementId, requestedBy, notes));
    }
}
