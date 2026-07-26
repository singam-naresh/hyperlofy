package com.hyperlofy.backend.merchant.dto;

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
@Schema(description = "Merchant Settlement Overview DTO")
public class MerchantSettlementDTO {

    @Schema(description = "Current Available Balance")
    private BigDecimal currentBalance;

    @Schema(description = "Lifetime Cumulative Earnings")
    private BigDecimal lifetimeEarnings;

    @Schema(description = "Pending Settlement Balance")
    private BigDecimal pendingSettlementAmount;

    @Schema(description = "Completed Settlement Records")
    private List<MerchantLedger> completedSettlements;

    @Schema(description = "Pending Unpaid Settlement Records")
    private List<MerchantLedger> pendingSettlements;

    @Schema(description = "Full Ledger History")
    private List<MerchantLedger> ledgerHistory;
}
