package com.hyperlofy.backend.delivery.dto;

import com.hyperlofy.backend.merchant.dto.SalesTrendDTO;
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
@Schema(description = "Delivery Partner Performance Analytics DTO")
public class DeliveryAnalyticsDTO {

    @Schema(description = "Acceptance Rate Percentage")
    private BigDecimal acceptanceRate;

    @Schema(description = "Completion Rate Percentage")
    private BigDecimal completionRate;

    @Schema(description = "Average Delivery Time in Minutes")
    private Double averageDeliveryTimeMinutes;

    @Schema(description = "Customer Rating")
    private BigDecimal customerRating;

    @Schema(description = "Late Deliveries Count")
    private Long lateDeliveriesCount;

    @Schema(description = "Cancelled Deliveries Count")
    private Long cancelledDeliveriesCount;

    @Schema(description = "Completed Deliveries Count")
    private Long completedDeliveriesCount;

    @Schema(description = "Total Distance Travelled in KM")
    private Double totalDistanceKm;

    @Schema(description = "Earnings Trend Data")
    private List<SalesTrendDTO> revenueTrend;

    @Schema(description = "Daily Performance Score")
    private BigDecimal dailyPerformance;

    @Schema(description = "Weekly Performance Score")
    private BigDecimal weeklyPerformance;

    @Schema(description = "Monthly Performance Score")
    private BigDecimal monthlyPerformance;
}
