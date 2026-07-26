package com.hyperlofy.backend.merchant.controller;

import com.hyperlofy.backend.merchant.dto.*;
import com.hyperlofy.backend.merchant.service.MerchantPortalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchant")
@RequiredArgsConstructor
@Tag(name = "Merchant Portal API", description = "Endpoints for Merchant Business Portal Dashboard, Analytics, and Profile Management")
@PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN', 'SUPER_ADMIN')")
public class MerchantPortalController {

    private final MerchantPortalService merchantPortalService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get Merchant Dashboard", description = "Retrieves consolidated merchant dashboard overview including orders, revenue, balance, ratings, and top products.")
    public ResponseEntity<MerchantDashboardDTO> getDashboard(@RequestParam UUID merchantId) {
        return ResponseEntity.ok(merchantPortalService.getDashboard(merchantId));
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get Merchant Analytics", description = "Retrieves merchant sales trends, peak ordering hours, repeat customer metrics, and growth statistics.")
    public ResponseEntity<MerchantAnalyticsDTO> getAnalytics(@RequestParam UUID merchantId) {
        return ResponseEntity.ok(merchantPortalService.getAnalytics(merchantId));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get Merchant Profile", description = "Retrieves merchant business details, store operating timings, contact info, and bank details.")
    public ResponseEntity<MerchantProfileDTO> getProfile(@RequestParam UUID merchantId) {
        return ResponseEntity.ok(merchantPortalService.getProfile(merchantId));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update Merchant Profile", description = "Updates merchant business profile information, store timings, contact details, and bank account information.")
    public ResponseEntity<MerchantProfileDTO> updateProfile(@RequestParam UUID merchantId, @Valid @RequestBody MerchantProfileDTO dto) {
        return ResponseEntity.ok(merchantPortalService.updateProfile(merchantId, dto));
    }
}
