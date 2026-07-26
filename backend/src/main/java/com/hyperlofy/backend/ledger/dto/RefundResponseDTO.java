package com.hyperlofy.backend.ledger.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO containing refund reconciliation financial breakdown")
public class RefundResponseDTO {

    @Schema(description = "Refund reconciliation ID")
    private UUID id;

    @Schema(description = "Order ID")
    private UUID orderId;

    @Schema(description = "Type of refund: FULL or PARTIAL")
    private String refundType;

    @Schema(description = "Status of escrow when refund occurred: HELD or RELEASED")
    private String escrowStatusAtRefund;

    @Schema(description = "Total order amount")
    private BigDecimal totalOrderAmount;

    @Schema(description = "Total refund amount issued to customer")
    private BigDecimal refundAmount;

    @Schema(description = "Merchant financial adjustment amount")
    private BigDecimal merchantAdjustment;

    @Schema(description = "Agent financial adjustment amount")
    private BigDecimal agentAdjustment;

    @Schema(description = "Platform financial adjustment amount")
    private BigDecimal platformAdjustment;

    @Schema(description = "Reconciliation execution status: COMPLETED or FAILED")
    private String status;

    @Schema(description = "Refund reason")
    private String reason;

    @Schema(description = "Reconciliation timestamp")
    private OffsetDateTime createdAt;
}
