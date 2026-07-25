package com.hyperlofy.backend.inventory.mapper;

import com.hyperlofy.backend.inventory.dto.InventoryAvailabilityResult;
import com.hyperlofy.backend.inventory.entity.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryAvailabilityResult toAvailability(Inventory inv) {
        if (inv == null) return null;
        return InventoryAvailabilityResult.builder()
                .merchantId(inv.getMerchantId())
                .productId(inv.getProductId())
                .sku(inv.getSku())
                .availableQuantity(inv.getAvailableQuantity())
                .reservedQuantity(inv.getReservedQuantity())
                .lowStockThreshold(inv.getLowStockThreshold())
                .available(inv.getAvailable())
                .updatedAt(inv.getUpdatedAt())
                .build();
    }
}
