package com.hyperlofy.backend.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservationResult {
    private UUID reservationId;
    private boolean success;
    private String message;
    private Integer reservedQuantity;
    private Integer availableQuantity;
    private OffsetDateTime updatedAt;
}
