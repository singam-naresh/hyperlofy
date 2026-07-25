package com.hyperlofy.backend.catalog.repository;

import com.hyperlofy.backend.catalog.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByMerchantId(UUID merchantId);
    java.util.Optional<Product> findBySku(String sku);
}
