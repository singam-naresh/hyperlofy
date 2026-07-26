package com.hyperlofy.backend.merchant.dto;

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
@Schema(description = "Single consolidated DTO for Merchant Dashboard")
public class MerchantDashboardDTO {

    @Schema(description = "Today's Orders Count")
    private Long todayOrdersCount;

    @Schema(description = "Pending Orders Count")
    private Long pendingOrdersCount;

    @Schema(description = "Preparing Orders Count")
    private Long preparingOrdersCount;

    @Schema(description = "Ready For Pickup Orders Count")
    private Long readyOrdersCount;

    @Schema(description = "Out For Delivery Orders Count")
    private Long outForDeliveryOrdersCount;

    @Schema(description = "Delivered Orders Count")
    private Long deliveredOrdersCount;

    @Schema(description = "Cancelled Orders Count")
    private Long cancelledOrdersCount;

    @Schema(description = "Today's Revenue")
    private BigDecimal todayRevenue;

    @Schema(description = "Weekly Revenue")
    private BigDecimal weeklyRevenue;

    @Schema(description = "Monthly Revenue")
    private BigDecimal monthlyRevenue;

    @Schema(description = "Total Lifetime Revenue")
    private BigDecimal totalRevenue;

    @Schema(description = "Current Balance")
    private BigDecimal currentBalance;

    @Schema(description = "Pending Settlement Balance")
    private BigDecimal settlementBalance;

    @Schema(description = "Average Order Value")
    private BigDecimal averageOrderValue;

    @Schema(description = "Merchant Rating")
    private BigDecimal rating;

    @Schema(description = "Order Completion Rate Percentage")
    private BigDecimal completionRate;

    @Schema(description = "Order Cancellation Rate Percentage")
    private BigDecimal cancellationRate;

    @Schema(description = "Top Selling Products")
    private List<TopProductDTO> topSellingProducts;
}
