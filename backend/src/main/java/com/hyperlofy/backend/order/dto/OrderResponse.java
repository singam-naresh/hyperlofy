package com.hyperlofy.backend.order.dto;

import com.hyperlofy.backend.order.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {
    private UUID id;
    private UUID customerId;
    private String customerName;
    private UUID agentId;
    private String agentName;
    private UUID zoneId;
    private String zoneName;
    private UUID merchantId;
    private String storeName;
    private Double storeLatitude;
    private Double storeLongitude;
    private String deliveryAddress;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
    private Double distanceKm;
    private BigDecimal deliveryFee;
    private String itemsDesc;
    private java.util.List<OrderItemDto> items;
    private OrderStatus orderStatus;
    private String otpCode;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
