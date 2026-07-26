package com.hyperlofy.backend.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Delivery Partner Dashboard Consolidated DTO")
public class DeliveryDashboardDTO {

    @Schema(description = "Today's Deliveries Count")
    private Long todayDeliveriesCount;

    @Schema(description = "Active Deliveries Count")
    private Long activeDeliveriesCount;

    @Schema(description = "Completed Deliveries Count")
    private Long completedDeliveriesCount;

    @Schema(description = "Cancelled Deliveries Count")
    private Long cancelledDeliveriesCount;

    @Schema(description = "Pending Assignments Count")
    private Long pendingAssignmentsCount;

    @Schema(description = "Today's Earnings")
    private BigDecimal todayEarnings;

    @Schema(description = "Weekly Earnings")
    private BigDecimal weeklyEarnings;

    @Schema(description = "Monthly Earnings")
    private BigDecimal monthlyEarnings;

    @Schema(description = "Current Wallet Balance")
    private BigDecimal currentWalletBalance;

    @Schema(description = "Pending Settlement Balance")
    private BigDecimal pendingSettlement;

    @Schema(description = "Lifetime Cumulative Earnings")
    private BigDecimal lifetimeEarnings;

    @Schema(description = "Average Partner Rating")
    private BigDecimal averageRating;

    @Schema(description = "Acceptance Rate Percentage")
    private BigDecimal acceptanceRate;

    @Schema(description = "Completion Rate Percentage")
    private BigDecimal completionRate;

    @Schema(description = "Average Delivery Time in Minutes")
    private Double averageDeliveryTimeMinutes;
}
