package com.hyperlofy.backend.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customer Loyalty & Rewards Overview DTO")
public class LoyaltyRewardsDTO {

    @Schema(description = "Reward Points Balance")
    private Integer rewardPointsBalance;

    @Schema(description = "Reward Tier Level", example = "GOLD")
    private String membershipTier;

    @Schema(description = "Total Lifetime Cashback Earned")
    private BigDecimal lifetimeCashbackEarned;

    @Schema(description = "Total Successful Referrals Count")
    private Integer successfulReferralsCount;

    @Schema(description = "Referral Code")
    private String referralCode;
}
