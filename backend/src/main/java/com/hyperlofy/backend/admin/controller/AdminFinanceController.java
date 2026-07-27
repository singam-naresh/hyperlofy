package com.hyperlofy.backend.admin.controller;

import com.hyperlofy.backend.admin.dto.AdminFinanceDashboardDTO;
import com.hyperlofy.backend.admin.service.AdminPlatformService;
import com.hyperlofy.backend.agent.entity.WithdrawalRequest;
import com.hyperlofy.backend.ledger.entity.CommissionLedger;
import com.hyperlofy.backend.ledger.entity.RefundReconciliation;
import com.hyperlofy.backend.merchant.entity.MerchantLedger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Financial Operations Monitoring API", description = "Read-only financial monitoring for platform escrow, revenue, refunds, merchant settlements, and agent payouts")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminFinanceController {

    private final AdminPlatformService adminPlatformService;

    @GetMapping("/finance/dashboard")
    @Operation(summary = "Get Financial Operations Dashboard", description = "Retrieves read-only financial metrics across escrow holding pool, platform revenue, pending settlements, and reconciled refunds.")
    public ResponseEntity<AdminFinanceDashboardDTO> getFinanceDashboard() {
        return ResponseEntity.ok(adminPlatformService.getFinanceDashboard());
    }

    @GetMapping("/refunds")
    @Operation(summary = "Get All Refund Reconciliations", description = "Retrieves system-wide refund reconciliation audit records.")
    public ResponseEntity<List<RefundReconciliation>> getRefunds() {
        return ResponseEntity.ok(adminPlatformService.getFinanceDashboard().getRecentRefunds());
    }

    @GetMapping("/escrow")
    @Operation(summary = "Get Escrow Pool Status", description = "Retrieves escrow pool holding statistics.")
    public ResponseEntity<AdminFinanceDashboardDTO> getEscrow() {
        return ResponseEntity.ok(adminPlatformService.getFinanceDashboard());
    }

    @GetMapping("/merchant-settlements")
    @Operation(summary = "Get Merchant Settlements Audit", description = "Retrieves merchant settlement ledger audit records.")
    public ResponseEntity<List<MerchantLedger>> getMerchantSettlements() {
        return ResponseEntity.ok(adminPlatformService.getFinanceDashboard().getMerchantLedgers());
    }

    @GetMapping("/agent-settlements")
    @Operation(summary = "Get Agent Settlements Audit", description = "Retrieves agent commission ledger audit records.")
    public ResponseEntity<List<CommissionLedger>> getAgentSettlements() {
        return ResponseEntity.ok(adminPlatformService.getFinanceDashboard().getCommissionLedgers());
    }

    @GetMapping("/ledger")
    @Operation(summary = "Get Immutable Financial Ledger", description = "Retrieves complete system financial ledger audit records.")
    public ResponseEntity<AdminFinanceDashboardDTO> getLedger() {
        return ResponseEntity.ok(adminPlatformService.getFinanceDashboard());
    }

    @GetMapping("/withdrawals")
    @Operation(summary = "Get Withdrawal Requests", description = "Retrieves partner withdrawal and bank payout requests.")
    public ResponseEntity<List<WithdrawalRequest>> getWithdrawals() {
        return ResponseEntity.ok(adminPlatformService.getFinanceDashboard().getWithdrawalRequests());
    }
}
