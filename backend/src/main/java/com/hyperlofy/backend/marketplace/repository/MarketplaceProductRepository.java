package com.hyperlofy.backend.marketplace.repository;

import com.hyperlofy.backend.marketplace.entity.MarketplaceProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MarketplaceProductRepository extends JpaRepository<MarketplaceProduct, UUID> {
    List<MarketplaceProduct> findByStoreId(UUID storeId);
    List<MarketplaceProduct> findByCategoryId(UUID categoryId);
    Optional<MarketplaceProduct> findBySku(String sku);
    List<MarketplaceProduct> findByProductNameContainingIgnoreCase(String keyword);
}
