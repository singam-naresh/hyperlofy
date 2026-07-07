package com.hyperlofy.backend.order.dto;

import com.hyperlofy.backend.order.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateRequest {

    @NotNull(message = "Next status is required")
    private OrderStatus nextStatus;

    private UUID agentId;
    private String otpCode;
    private String remarks;
}
