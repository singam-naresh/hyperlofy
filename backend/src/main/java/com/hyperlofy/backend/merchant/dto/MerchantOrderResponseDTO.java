package com.hyperlofy.backend.merchant.dto;

import com.hyperlofy.backend.order.dto.OrderItemDto;
import com.hyperlofy.backend.order.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Merchant Order View DTO")
public class MerchantOrderResponseDTO {

    @Schema(description = "Order ID")
    private UUID orderId;

    @Schema(description = "Merchant ID")
    private UUID merchantId;

    @Schema(description = "Store Name")
    private String storeName;

    @Schema(description = "Order Status")
    private OrderStatus status;

    @Schema(description = "Delivery Address")
    private String deliveryAddress;

    @Schema(description = "Order Items")
    private List<OrderItemDto> items;

    @Schema(description = "Delivery Fee")
    private BigDecimal deliveryFee;

    @Schema(description = "Total Amount")
    private BigDecimal totalAmount;

    @Schema(description = "Order Creation Timestamp")
    private OffsetDateTime createdAt;

    @Schema(description = "Last Updated Timestamp")
    private OffsetDateTime updatedAt;
}
