package com.hyperlofy.backend.customer.dto;

import com.hyperlofy.backend.customer.entity.CustomerAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Checkout Preview Breakdown DTO")
public class CheckoutPreviewDTO {

    @Schema(description = "Selected Delivery Address")
    private CustomerAddress deliveryAddress;

    @Schema(description = "Merchant Store ID")
    private UUID merchantId;

    @Schema(description = "Items Subtotal")
    private BigDecimal itemSubtotal;

    @Schema(description = "Discount Amount")
    private BigDecimal discountAmount;

    @Schema(description = "Delivery Fee")
    private BigDecimal deliveryFee;

    @Schema(description = "Taxes & Platform Fees")
    private BigDecimal taxesAndFees;

    @Schema(description = "Wallet Discount / Applied Balance")
    private BigDecimal walletDeduction;

    @Schema(description = "Final Amount Payable")
    private BigDecimal amountPayable;

    @Schema(description = "Escrow Holding Placement Amount")
    private BigDecimal escrowPlacementAmount;

    @Schema(description = "Estimated Arrival Time Minutes", example = "30")
    private Integer estimatedArrivalMinutes;
}
