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
@Schema(description = "Sales trend data point DTO")
public class SalesTrendDTO {
    @Schema(description = "Time period label (e.g. date or hour)")
    private String period;

    @Schema(description = "Revenue in period")
    private BigDecimal revenue;

    @Schema(description = "Order count in period")
    private Long orderCount;
}
