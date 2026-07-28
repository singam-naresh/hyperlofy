package com.hyperlofy.backend.merchant.controller;

import com.hyperlofy.backend.marketplace.entity.MarketplaceProduct;
import com.hyperlofy.backend.marketplace.service.MarketplaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchant/catalog")
@RequiredArgsConstructor
@Tag(name = "Merchant Product Catalog Management API", description = "Endpoints for managing store product listings, prices, and stock units")
@PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN', 'SUPER_ADMIN')")
public class MerchantCatalogController {

    private final MarketplaceService marketplaceService;

    @PostMapping("/products")
    @Operation(summary = "Create Catalog Product", description = "Adds a new product entry to the merchant's store catalog.")
    public ResponseEntity<MarketplaceProduct> createProduct(@RequestBody MarketplaceProduct product, Principal principal) {
        product.setMerchantId(UUID.randomUUID());
        return ResponseEntity.ok(marketplaceService.createProduct(product));
    }
}
