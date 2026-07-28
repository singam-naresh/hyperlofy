package com.hyperlofy.backend.merchant.repository;

import com.hyperlofy.backend.merchant.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {
    List<Store> findByMerchantId(UUID merchantId);
    List<Store> findByCityAndStoreStatus(String city, String storeStatus);
}
