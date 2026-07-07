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
public class OperationalMetrics {
    private double successRatePercent;
    private double cancellationRatePercent;
    private double refundRatePercent;
    private double failureRatePercent;
    private int totalDispatchedOrders;
}
