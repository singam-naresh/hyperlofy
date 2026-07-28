package com.hyperlofy.backend.customer.controller;

import com.hyperlofy.backend.marketplace.entity.InventoryReservation;
import com.hyperlofy.backend.marketplace.entity.MarketplaceProduct;
import com.hyperlofy.backend.marketplace.service.MarketplaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer/marketplace")
@RequiredArgsConstructor
@Tag(name = "Customer Marketplace Engine API", description = "Endpoints for discovering nearby store products, product search, filtering, and inventory reservation")
@PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
public class CustomerMarketplaceController {

    private final MarketplaceService marketplaceService;

    @GetMapping("/store/{storeId}/products")
    @Operation(summary = "Get Store Catalog Products", description = "Retrieves active marketplace products and variants for a specific store.")
    public ResponseEntity<List<MarketplaceProduct>> getStoreProducts(@PathVariable UUID storeId) {
        return ResponseEntity.ok(marketplaceService.getStoreProducts(storeId));
    }

    @GetMapping("/search")
    @Operation(summary = "Search Marketplace Products", description = "Searches active products by keyword, category, or brand across available stores.")
    public ResponseEntity<List<MarketplaceProduct>> searchProducts(@RequestParam String q) {
        return ResponseEntity.ok(marketplaceService.searchProducts(q));
    }

    @PostMapping("/reserve")
    @Operation(summary = "Reserve Inventory", description = "Holds inventory quantity for a customer during active cart session before order checkout.")
    public ResponseEntity<InventoryReservation> reserveInventory(
            Principal principal,
            @RequestParam UUID variantId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(marketplaceService.reserveInventory(UUID.randomUUID(), variantId, quantity));
    }
}
