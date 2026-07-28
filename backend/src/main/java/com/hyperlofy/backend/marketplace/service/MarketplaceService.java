package com.hyperlofy.backend.marketplace.service;

import com.hyperlofy.backend.marketplace.entity.Inventory;
import com.hyperlofy.backend.marketplace.entity.InventoryReservation;
import com.hyperlofy.backend.marketplace.entity.MarketplaceProduct;
import com.hyperlofy.backend.marketplace.entity.ProductVariant;
import com.hyperlofy.backend.marketplace.repository.InventoryRepository;
import com.hyperlofy.backend.marketplace.repository.InventoryReservationRepository;
import com.hyperlofy.backend.marketplace.repository.MarketplaceProductRepository;
import com.hyperlofy.backend.marketplace.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MarketplaceService {

    private static final Logger log = LoggerFactory.getLogger(MarketplaceService.class);

    private final MarketplaceProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryReservationRepository reservationRepository;

    @Transactional
    @CacheEvict(value = "marketplace_products", allEntries = true)
    public MarketplaceProduct createProduct(MarketplaceProduct product) {
        log.info("Creating marketplace product: name={}, storeId={}", product.getProductName(), product.getStoreId());
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "marketplace_products", key = "'store_' + #storeId")
    public List<MarketplaceProduct> getStoreProducts(UUID storeId) {
        return productRepository.findByStoreId(storeId);
    }

    @Transactional(readOnly = true)
    public List<MarketplaceProduct> searchProducts(String query) {
        return productRepository.findByProductNameContainingIgnoreCase(query);
    }

    @Transactional
    public InventoryReservation reserveInventory(UUID customerId, UUID variantId, int quantity) {
        log.info("Reserving inventory: customerId={}, variantId={}, qty={}", customerId, variantId, quantity);

        Inventory inv = inventoryRepository.findByVariantId(variantId).orElseGet(() ->
                Inventory.builder()
                        .variantId(variantId)
                        .availableStock(100)
                        .reservedStock(0)
                        .build()
        );

        if (inv.getAvailableStock() < quantity) {
            throw new IllegalArgumentException("Insufficient stock available for variantId: " + variantId);
        }

        inv.setAvailableStock(inv.getAvailableStock() - quantity);
        inv.setReservedStock(inv.getReservedStock() + quantity);
        inventoryRepository.save(inv);

        InventoryReservation reservation = InventoryReservation.builder()
                .customerId(customerId)
                .variantId(variantId)
                .reservedQuantity(quantity)
                .reservationStatus("RESERVED")
                .expiresAt(ZonedDateTime.now().plusMinutes(15))
                .build();

        return reservationRepository.save(reservation);
    }
}
