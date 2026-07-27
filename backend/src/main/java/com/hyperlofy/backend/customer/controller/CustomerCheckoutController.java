package com.hyperlofy.backend.customer.controller;

import com.hyperlofy.backend.customer.dto.CheckoutPreviewDTO;
import com.hyperlofy.backend.customer.service.CustomerExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer/checkout")
@RequiredArgsConstructor
@Tag(name = "Customer Checkout Experience API", description = "Endpoints for checkout preview calculation, address selection, wallet deductions, and escrow placement estimation")
@PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
public class CustomerCheckoutController {

    private final CustomerExperienceService customerService;

    @GetMapping("/preview")
    @Operation(summary = "Get Checkout Preview Breakdown", description = "Calculates final payable amount, delivery fee, taxes, wallet deductions, and escrow holding placement.")
    public ResponseEntity<CheckoutPreviewDTO> getCheckoutPreview(
            Principal principal,
            @RequestParam(required = false) UUID addressId,
            @RequestParam(defaultValue = "false") boolean useWallet) {

        return ResponseEntity.ok(customerService.getCheckoutPreview(UUID.randomUUID(), addressId, useWallet));
    }
}
