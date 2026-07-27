package com.hyperlofy.backend.customer.controller;

import com.hyperlofy.backend.customer.dto.CartSummaryDTO;
import com.hyperlofy.backend.customer.service.CustomerExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer/cart")
@RequiredArgsConstructor
@Tag(name = "Customer Shopping Cart API", description = "Endpoints for cart item management, quantity updates, price breakdown, and tax calculation")
@PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
public class CustomerCartController {

    private final CustomerExperienceService customerService;

    @GetMapping
    @Operation(summary = "Get Shopping Cart Summary", description = "Retrieves active shopping cart contents and price breakdown.")
    public ResponseEntity<CartSummaryDTO> getCartSummary(Principal principal) {
        return ResponseEntity.ok(customerService.getCartSummary(UUID.randomUUID()));
    }

    @PostMapping("/items")
    @Operation(summary = "Add Item to Cart", description = "Adds an item to customer shopping cart.")
    public ResponseEntity<CartSummaryDTO> addItemToCart(
            Principal principal,
            @RequestParam UUID merchantId,
            @RequestParam UUID productId,
            @RequestParam String productName,
            @RequestParam BigDecimal unitPrice,
            @RequestParam(defaultValue = "1") Integer quantity) {

        return ResponseEntity.ok(customerService.addItemToCart(UUID.randomUUID(), merchantId, productId, productName, unitPrice, quantity));
    }
}
