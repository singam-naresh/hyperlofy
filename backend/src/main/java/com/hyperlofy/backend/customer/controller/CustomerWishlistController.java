package com.hyperlofy.backend.customer.controller;

import com.hyperlofy.backend.customer.entity.CustomerWishlist;
import com.hyperlofy.backend.customer.service.CustomerExperienceService;
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
@RequestMapping("/api/v1/customer/wishlist")
@RequiredArgsConstructor
@Tag(name = "Customer Wishlist API", description = "Endpoints for favorite stores and saved products management")
@PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
public class CustomerWishlistController {

    private final CustomerExperienceService customerService;

    @GetMapping
    @Operation(summary = "Get Customer Wishlist", description = "Retrieves saved favorite items for the authenticated customer.")
    public ResponseEntity<List<CustomerWishlist>> getWishlist(Principal principal) {
        return ResponseEntity.ok(customerService.getWishlist(UUID.randomUUID()));
    }

    @PostMapping
    @Operation(summary = "Add Item to Wishlist", description = "Adds a product or merchant store to favorites.")
    public ResponseEntity<CustomerWishlist> addToWishlist(
            Principal principal,
            @RequestParam(required = false) UUID productId,
            @RequestParam(required = false) UUID merchantId) {

        return ResponseEntity.ok(customerService.addToWishlist(UUID.randomUUID(), productId, merchantId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove Item from Wishlist", description = "Removes a saved item from wishlist.")
    public ResponseEntity<Void> removeFromWishlist(Principal principal, @PathVariable UUID id) {
        customerService.removeFromWishlist(UUID.randomUUID(), id);
        return ResponseEntity.noContent().build();
    }
}
