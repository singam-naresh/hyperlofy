package com.hyperlofy.backend.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservationRequest {
    private UUID reservationId;
    private UUID merchantId;
    private UUID productId;
    private String sku;
    private Integer quantity;
}
