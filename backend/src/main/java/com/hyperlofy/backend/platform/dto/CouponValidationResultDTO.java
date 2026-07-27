package com.hyperlofy.backend.platform.dto;

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
@Schema(description = "Coupon Validation & Preview Calculation Result DTO")
public class CouponValidationResultDTO {

    @Schema(description = "Validity Status Flag")
    private boolean valid;

    @Schema(description = "Coupon Code")
    private String code;

    @Schema(description = "Discount Amount Calculated")
    private BigDecimal discountAmount;

    @Schema(description = "Final Total Order Amount After Discount")
    private BigDecimal finalOrderAmount;

    @Schema(description = "Validation Message or Rejection Reason")
    private String message;
}
