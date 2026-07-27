package com.hyperlofy.backend.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Fraud & Risk Monitoring Analytics DTO")
public class FraudRiskDashboardDTO {

    @Schema(description = "Suspected Duplicate Accounts Count")
    private Long duplicateAccountsCount;

    @Schema(description = "High Refund Claiming Customers Count")
    private Long highRefundCustomersCount;

    @Schema(description = "High Order Cancellation Merchants Count")
    private Long highCancellationMerchantsCount;

    @Schema(description = "Payment Failure Rate Percentage")
    private Double paymentFailureRate;

    @Schema(description = "Suspicious Flagged Orders Count")
    private Long suspiciousOrdersCount;

    @Schema(description = "System Overall Fraud Risk Score (0-100)")
    private Integer overallFraudScore;

    @Schema(description = "System Security Risk Rating", example = "LOW")
    private String riskRating;
}
