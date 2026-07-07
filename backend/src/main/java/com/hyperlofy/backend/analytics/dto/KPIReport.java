package com.hyperlofy.backend.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KPIReport {
    private int totalOrders;
    private BigDecimal totalRevenue;
    private int onlineAgents;
    private BigDecimal platformRevenue;
    private BigDecimal escrowBalance;
}
