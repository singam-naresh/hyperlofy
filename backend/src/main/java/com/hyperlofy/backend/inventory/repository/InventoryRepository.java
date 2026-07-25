package com.hyperlofy.backend.inventory.repository;

import com.hyperlofy.backend.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    Optional<Inventory> findByMerchantIdAndProductId(UUID merchantId, UUID productId);
    Optional<Inventory> findByMerchantIdAndSku(UUID merchantId, String sku);
}
