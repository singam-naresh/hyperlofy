package com.hyperlofy.backend.delivery.dto;

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
@Schema(description = "Delivery Order View DTO")
public class DeliveryOrderResponseDTO {

    @Schema(description = "Order ID")
    private UUID orderId;

    @Schema(description = "Store Name")
    private String storeName;

    @Schema(description = "Store Pickup Address")
    private String storeAddress;

    @Schema(description = "Store Latitude")
    private Double storeLatitude;

    @Schema(description = "Store Longitude")
    private Double storeLongitude;

    @Schema(description = "Customer Delivery Address")
    private String deliveryAddress;

    @Schema(description = "Customer Latitude")
    private Double deliveryLatitude;

    @Schema(description = "Customer Longitude")
    private Double deliveryLongitude;

    @Schema(description = "Trip Distance in KM")
    private Double distanceKm;

    @Schema(description = "Delivery Fee Earned")
    private BigDecimal deliveryFee;

    @Schema(description = "Order Status")
    private OrderStatus status;

    @Schema(description = "OTP Verification Code (Visible when Arrived Customer)")
    private String otpCode;

    @Schema(description = "Order Items")
    private List<OrderItemDto> items;

    @Schema(description = "Order Created Timestamp")
    private OffsetDateTime createdAt;

    @Schema(description = "Order Last Updated Timestamp")
    private OffsetDateTime updatedAt;
}
