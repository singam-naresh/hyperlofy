package com.hyperlofy.backend.admin.dto;

import com.hyperlofy.backend.agent.entity.WithdrawalRequest;
import com.hyperlofy.backend.ledger.entity.CommissionLedger;
import com.hyperlofy.backend.ledger.entity.RefundReconciliation;
import com.hyperlofy.backend.merchant.entity.MerchantLedger;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Admin Financial Operations Dashboard DTO")
public class AdminFinanceDashboardDTO {

    @Schema(description = "Total Escrow Holding Pool Balance")
    private BigDecimal escrowPoolBalance;

    @Schema(description = "Total Platform Revenue")
    private BigDecimal platformRevenue;

    @Schema(description = "Total Merchant Unpaid Balance")
    private BigDecimal pendingMerchantSettlementBalance;

    @Schema(description = "Total Agent Unpaid Balance")
    private BigDecimal pendingAgentSettlementBalance;

    @Schema(description = "Total Refund Amount Reconciled")
    private BigDecimal totalRefundsReconciled;

    @Schema(description = "Recent Refund Reconciliations")
    private List<RefundReconciliation> recentRefunds;

    @Schema(description = "Recent Merchant Ledgers")
    private List<MerchantLedger> merchantLedgers;

    @Schema(description = "Recent Commission Ledgers")
    private List<CommissionLedger> commissionLedgers;

    @Schema(description = "Recent Withdrawal Requests")
    private List<WithdrawalRequest> withdrawalRequests;
}
