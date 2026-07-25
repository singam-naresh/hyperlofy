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
public class InventoryAvailabilityResult {
    private UUID merchantId;
    private UUID productId;
    private String sku;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private Integer lowStockThreshold;
    private Boolean available;
    private OffsetDateTime updatedAt;
}
