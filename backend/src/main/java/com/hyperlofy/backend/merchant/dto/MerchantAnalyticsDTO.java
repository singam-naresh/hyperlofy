package com.hyperlofy.backend.merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Merchant Analytics DTO")
public class MerchantAnalyticsDTO {

    @Schema(description = "Daily Sales Amount")
    private BigDecimal dailySales;

    @Schema(description = "Weekly Sales Amount")
    private BigDecimal weeklySales;

    @Schema(description = "Monthly Sales Amount")
    private BigDecimal monthlySales;

    @Schema(description = "Revenue Trend Data Points")
    private List<SalesTrendDTO> revenueTrends;

    @Schema(description = "Top Selling Products")
    private List<TopProductDTO> topSellingProducts;

    @Schema(description = "Peak Ordering Hours (Hour -> Order Count)")
    private Map<Integer, Long> peakOrderingHours;

    @Schema(description = "Repeat Customer Count")
    private Long repeatCustomerCount;

    @Schema(description = "Average Order Value")
    private BigDecimal averageOrderValue;

    @Schema(description = "Order Completion Rate (%)")
    private BigDecimal orderCompletionRate;

    @Schema(description = "Cancellation Rate (%)")
    private BigDecimal cancellationRate;

    @Schema(description = "Revenue Growth Percentage")
    private BigDecimal growthPercentage;
}
