package com.hyperlofy.backend.ledger.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for order refund reconciliation processing")
public class RefundRequestDTO {

    @DecimalMin(value = "0.01", message = "Refund amount must be greater than 0")
    @Schema(description = "Optional partial refund amount. If omitted, a full refund is processed.", example = "250.00")
    private BigDecimal refundAmount;

    @Schema(description = "Reason for processing the refund", example = "Customer cancellation / item defective")
    private String reason;
}
