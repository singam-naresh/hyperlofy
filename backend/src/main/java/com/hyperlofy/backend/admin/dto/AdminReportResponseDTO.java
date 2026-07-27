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
@Schema(description = "CSV-ready Admin Operational Report Summary DTO")
public class AdminReportResponseDTO {

    @Schema(description = "Report Type Category", example = "REVENUE_SUMMARY")
    private String reportType;

    @Schema(description = "Report Time Window", example = "2026-07-01 to 2026-07-26")
    private String periodWindow;

    @Schema(description = "Total Metric Count")
    private Long totalCount;

    @Schema(description = "Total Financial Valuation")
    private BigDecimal totalValuation;

    @Schema(description = "Average Transaction Value")
    private BigDecimal averageValue;

    @Schema(description = "Report Summary Description")
    private String summaryText;
}
