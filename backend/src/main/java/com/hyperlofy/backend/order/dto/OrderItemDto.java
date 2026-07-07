package com.hyperlofy.backend.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private UUID id;
    private String itemName;
    private int quantity;
    private BigDecimal estimatedPrice;
    private BigDecimal finalPrice;
    private String itemStatus;
}
