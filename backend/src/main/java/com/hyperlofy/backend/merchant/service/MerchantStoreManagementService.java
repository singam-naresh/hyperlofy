package com.hyperlofy.backend.merchant.service;

import com.hyperlofy.backend.merchant.entity.Store;
import com.hyperlofy.backend.merchant.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MerchantStoreManagementService {

    private static final Logger log = LoggerFactory.getLogger(MerchantStoreManagementService.class);

    private final StoreRepository storeRepository;

    @Transactional
    @CacheEvict(value = "merchant_stores", key = "'merchant_' + #store.merchantId")
    public Store createStore(Store store) {
        log.info("Creating new store: name={}, merchantId={}", store.getStoreName(), store.getMerchantId());
        return storeRepository.save(store);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "merchant_stores", key = "'merchant_' + #merchantId")
    public List<Store> getMerchantStores(UUID merchantId) {
        return storeRepository.findByMerchantId(merchantId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getMerchantDashboardMetrics(UUID merchantId) {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("ordersToday", 28);
        metrics.put("revenueToday", 8450.00);
        metrics.put("pendingOrders", 3);
        metrics.put("completedOrders", 25);
        metrics.put("storeRating", 4.85);
        metrics.put("onlineStatus", "ONLINE");
        return metrics;
    }
}
