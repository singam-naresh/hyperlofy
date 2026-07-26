package com.hyperlofy.backend.delivery.dto;

import com.hyperlofy.backend.agent.entity.WithdrawalRequest;
import com.hyperlofy.backend.ledger.entity.CommissionLedger;
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
@Schema(description = "Delivery Partner Earnings and Settlement Overview DTO")
public class DeliveryEarningsDTO {

    @Schema(description = "Today's Earnings")
    private BigDecimal todayEarnings;

    @Schema(description = "Weekly Earnings")
    private BigDecimal weeklyEarnings;

    @Schema(description = "Monthly Earnings")
    private BigDecimal monthlyEarnings;

    @Schema(description = "Lifetime Cumulative Earnings")
    private BigDecimal lifetimeEarnings;

    @Schema(description = "Current Available Balance")
    private BigDecimal currentBalance;

    @Schema(description = "Pending Settlement Balance")
    private BigDecimal pendingSettlementAmount;

    @Schema(description = "Commission Ledger History")
    private List<CommissionLedger> ledgerHistory;

    @Schema(description = "Payout & Withdrawal History")
    private List<WithdrawalRequest> payoutHistory;
}
