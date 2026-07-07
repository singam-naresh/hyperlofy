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
public class RevenueReport {
    private BigDecimal dailyRevenue;
    private BigDecimal weeklyRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal totalPlatformEarnings;
    private BigDecimal totalAgentPayouts;
}
