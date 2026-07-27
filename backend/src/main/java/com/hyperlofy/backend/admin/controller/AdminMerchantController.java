package com.hyperlofy.backend.admin.controller;

import com.hyperlofy.backend.admin.service.AdminPlatformService;
import com.hyperlofy.backend.merchant.dto.MerchantAnalyticsDTO;
import com.hyperlofy.backend.merchant.dto.MerchantSettlementDTO;
import com.hyperlofy.backend.merchant.entity.MerchantLedger;
import com.hyperlofy.backend.merchant.entity.MerchantProfile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/merchants")
@RequiredArgsConstructor
@Tag(name = "Admin Merchant Administration API", description = "Endpoints for merchant lifecycle management, suspension, activation, analytics, and ledger auditing")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminMerchantController {

    private final AdminPlatformService adminPlatformService;

    @GetMapping
    @Operation(summary = "List Merchants", description = "Paginated lookup of system merchants with search filtering.")
    public ResponseEntity<Page<MerchantProfile>> getMerchants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {

        return ResponseEntity.ok(adminPlatformService.getMerchants(page, size, search));
    }

    @GetMapping("/{merchantId}")
    @Operation(summary = "Get Merchant Profile", description = "Retrieves complete merchant profile details.")
    public ResponseEntity<MerchantProfile> getMerchantById(@PathVariable UUID merchantId) {
        return ResponseEntity.ok(adminPlatformService.getMerchantById(merchantId));
    }

    @PatchMapping("/{merchantId}/activate")
    @Operation(summary = "Activate Merchant", description = "Activates a merchant store profile.")
    public ResponseEntity<MerchantProfile> activateMerchant(Principal principal, @PathVariable UUID merchantId) {
        return ResponseEntity.ok(adminPlatformService.setMerchantActive(UUID.randomUUID(), principal.getName(), merchantId, true, "Admin activated merchant"));
    }

    @PatchMapping("/{merchantId}/suspend")
    @Operation(summary = "Suspend Merchant", description = "Suspends a merchant store profile.")
    public ResponseEntity<MerchantProfile> suspendMerchant(
            Principal principal,
            @PathVariable UUID merchantId,
            @RequestParam(defaultValue = "Policy violation or administrative action") String reason) {

        return ResponseEntity.ok(adminPlatformService.setMerchantActive(UUID.randomUUID(), principal.getName(), merchantId, false, reason));
    }

    @PatchMapping("/{merchantId}/reactivate")
    @Operation(summary = "Reactivate Merchant", description = "Reactivates a suspended merchant store profile.")
    public ResponseEntity<MerchantProfile> reactivateMerchant(Principal principal, @PathVariable UUID merchantId) {
        return ResponseEntity.ok(adminPlatformService.setMerchantActive(UUID.randomUUID(), principal.getName(), merchantId, true, "Admin reactivated merchant"));
    }

    @GetMapping("/{merchantId}/analytics")
    @Operation(summary = "Get Merchant Analytics", description = "Retrieves performance analytics for a merchant store.")
    public ResponseEntity<MerchantAnalyticsDTO> getMerchantAnalytics(@PathVariable UUID merchantId) {
        return ResponseEntity.ok(adminPlatformService.getMerchantAnalytics(merchantId));
    }

    @GetMapping("/{merchantId}/ledger")
    @Operation(summary = "Get Merchant Ledger", description = "Retrieves ledger history for a merchant.")
    public ResponseEntity<List<MerchantLedger>> getMerchantLedgers(@PathVariable UUID merchantId) {
        return ResponseEntity.ok(adminPlatformService.getMerchantLedgers(merchantId));
    }

    @GetMapping("/{merchantId}/settlements")
    @Operation(summary = "Get Merchant Settlement Overview", description = "Retrieves settlement details for a merchant.")
    public ResponseEntity<MerchantSettlementDTO> getMerchantSettlements(@PathVariable UUID merchantId) {
        return ResponseEntity.ok(adminPlatformService.getMerchantSettlements(merchantId));
    }
}
