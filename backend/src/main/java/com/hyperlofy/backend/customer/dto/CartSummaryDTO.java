package com.hyperlofy.backend.customer.dto;

import com.hyperlofy.backend.customer.entity.CustomerCartItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Shopping Cart Summary & Price Breakdown DTO")
public class CartSummaryDTO {

    @Schema(description = "Cart ID")
    private UUID cartId;

    @Schema(description = "Merchant Store ID")
    private UUID merchantId;

    @Schema(description = "Cart Items List")
    private List<CustomerCartItem> items;

    @Schema(description = "Item Subtotal")
    private BigDecimal itemSubtotal;

    @Schema(description = "Applied Coupon Code")
    private String couponCode;

    @Schema(description = "Coupon Discount Amount")
    private BigDecimal discountAmount;

    @Schema(description = "Estimated Taxes & Platform Fee")
    private BigDecimal estimatedTaxes;

    @Schema(description = "Estimated Delivery Fee")
    private BigDecimal deliveryFee;

    @Schema(description = "Final Order Total Amount")
    private BigDecimal finalTotal;
}
