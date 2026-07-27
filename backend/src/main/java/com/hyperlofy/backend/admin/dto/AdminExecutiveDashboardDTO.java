package com.hyperlofy.backend.admin.dto;

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
@Schema(description = "Admin Executive Operations Dashboard DTO")
public class AdminExecutiveDashboardDTO {

    @Schema(description = "Total System Orders")
    private Long totalOrders;

    @Schema(description = "Today's Orders")
    private Long todayOrders;

    @Schema(description = "Pending Orders Count")
    private Long pendingOrders;

    @Schema(description = "Preparing Orders Count")
    private Long preparingOrders;

    @Schema(description = "Out For Delivery Orders Count")
    private Long outForDeliveryOrders;

    @Schema(description = "Delivered Orders Count")
    private Long deliveredOrders;

    @Schema(description = "Cancelled Orders Count")
    private Long cancelledOrders;

    @Schema(description = "Refunded Orders Count")
    private Long refundedOrders;

    @Schema(description = "Registered Customers Count")
    private Long registeredCustomers;

    @Schema(description = "Active Merchants Count")
    private Long activeMerchants;

    @Schema(description = "Active Delivery Partners Count")
    private Long activeDeliveryPartners;

    @Schema(description = "Online Delivery Partners Count")
    private Long onlineDeliveryPartners;

    @Schema(description = "Today's Gross Revenue")
    private BigDecimal todayRevenue;

    @Schema(description = "Weekly Gross Revenue")
    private BigDecimal weeklyRevenue;

    @Schema(description = "Monthly Gross Revenue")
    private BigDecimal monthlyRevenue;

    @Schema(description = "Total Platform Commission Earned")
    private BigDecimal platformCommission;

    @Schema(description = "Pending Refund Requests Count")
    private Long pendingRefunds;

    @Schema(description = "Pending Merchant Settlements Count")
    private Long pendingSettlements;

    @Schema(description = "Open Withdrawal Requests Count")
    private Long openWithdrawals;

    @Schema(description = "Low Stock Inventory Products Count")
    private Long lowStockProductsCount;

    @Schema(description = "System Health Summary", example = "HEALTHY")
    private String systemHealthSummary;
}
