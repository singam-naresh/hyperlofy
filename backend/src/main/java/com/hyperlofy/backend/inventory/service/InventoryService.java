package com.hyperlofy.backend.inventory.service;

import com.hyperlofy.backend.catalog.service.ProductService;
import com.hyperlofy.backend.inventory.dto.InventoryAvailabilityResult;
import com.hyperlofy.backend.inventory.entity.Inventory;
import com.hyperlofy.backend.inventory.mapper.InventoryMapper;
import com.hyperlofy.backend.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductService productService;
    private final InventoryMapper inventoryMapper;

    @Transactional(readOnly = true)
    public InventoryAvailabilityResult checkAvailability(UUID merchantId, UUID productId) {
        Optional<Inventory> inv = inventoryRepository.findByMerchantIdAndProductId(merchantId, productId);
        return inv.map(inventoryMapper::toAvailability).orElse(null);
    }

    @Transactional(readOnly = true)
    public InventoryAvailabilityResult checkAvailabilityBySku(UUID merchantId, String sku) {
        Optional<Inventory> inv = inventoryRepository.findByMerchantIdAndSku(merchantId, sku);
        return inv.map(inventoryMapper::toAvailability).orElse(null);
    }

    @Transactional(readOnly = true)
    public int getAvailableQuantity(UUID merchantId, UUID productId) {
        return inventoryRepository.findByMerchantIdAndProductId(merchantId, productId)
                .map(i -> Math.max(0, i.getAvailableQuantity() - i.getReservedQuantity()))
                .orElse(0);
    }

    @Transactional(readOnly = true)
    public boolean isAvailable(UUID merchantId, UUID productId, int required) {
        return getAvailableQuantity(merchantId, productId) >= required;
    }
}
