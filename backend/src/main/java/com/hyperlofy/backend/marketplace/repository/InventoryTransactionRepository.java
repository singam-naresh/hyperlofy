package com.hyperlofy.backend.marketplace.repository;

import com.hyperlofy.backend.marketplace.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, UUID> {
    List<InventoryTransaction> findByVariantIdOrderByCreatedAtDesc(UUID variantId);
}
