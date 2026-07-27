package com.hyperlofy.backend.event.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Real-Time Order Status Change Payload DTO")
public class OrderStatusEventDTO {

    @Schema(description = "Order ID")
    private UUID orderId;

    @Schema(description = "Previous Status")
    private String previousStatus;

    @Schema(description = "New Order Status", example = "PREPARING")
    private String newStatus;

    @Schema(description = "Customer ID")
    private UUID customerId;

    @Schema(description = "Merchant Store ID")
    private UUID merchantId;

    @Schema(description = "Assigned Delivery Driver Agent ID")
    private UUID agentId;
}
