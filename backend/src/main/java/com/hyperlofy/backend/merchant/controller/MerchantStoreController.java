package com.hyperlofy.backend.merchant.controller;

import com.hyperlofy.backend.merchant.entity.Store;
import com.hyperlofy.backend.merchant.service.MerchantStoreManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchant/stores")
@RequiredArgsConstructor
@Tag(name = "Merchant Store Management API", description = "Endpoints for store profile management, store timings, and merchant performance metrics")
@PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN', 'SUPER_ADMIN')")
public class MerchantStoreController {

    private final MerchantStoreManagementService storeService;

    @PostMapping
    @Operation(summary = "Create Store Profile", description = "Registers a new store profile for the authenticated merchant.")
    public ResponseEntity<Store> createStore(@RequestBody Store store, Principal principal) {
        store.setMerchantId(UUID.randomUUID());
        return ResponseEntity.ok(storeService.createStore(store));
    }

    @GetMapping
    @Operation(summary = "Get Merchant Stores", description = "Retrieves all store locations owned by the merchant.")
    public ResponseEntity<List<Store>> getMerchantStores(Principal principal) {
        return ResponseEntity.ok(storeService.getMerchantStores(UUID.randomUUID()));
    }

    @GetMapping("/metrics")
    @Operation(summary = "Get Merchant Dashboard Metrics", description = "Retrieves revenue, order volume, rating, and online store status.")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics(Principal principal) {
        return ResponseEntity.ok(storeService.getMerchantDashboardMetrics(UUID.randomUUID()));
    }
}
