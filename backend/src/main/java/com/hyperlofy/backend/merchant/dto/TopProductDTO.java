package com.hyperlofy.backend.merchant.dto;

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
@Schema(description = "Top selling product metric DTO")
public class TopProductDTO {
    @Schema(description = "Product Name")
    private String productName;

    @Schema(description = "Total Quantity Sold")
    private Long totalQuantity;

    @Schema(description = "Total Gross Revenue Generated")
    private BigDecimal totalSales;
}
