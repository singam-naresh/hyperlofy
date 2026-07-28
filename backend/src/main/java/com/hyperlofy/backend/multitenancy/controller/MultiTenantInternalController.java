package com.hyperlofy.backend.multitenancy.controller;

import com.hyperlofy.backend.multitenancy.entity.Tenant;
import com.hyperlofy.backend.multitenancy.entity.TenantBranding;
import com.hyperlofy.backend.multitenancy.entity.TenantSubscription;
import com.hyperlofy.backend.multitenancy.service.MultiTenantPlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenant/internal")
@RequiredArgsConstructor
@Tag(name = "Multi-Tenancy & SaaS Platform Internal API", description = "Endpoints for automated tenant provisioning, white-label branding configurations, and SaaS subscription entitlement management")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class MultiTenantInternalController {

    private final MultiTenantPlatformService tenantService;

    @PostMapping("/provision")
    @Operation(summary = "Provision Multi-Tenant SaaS Entity", description = "Provisions new brand, franchise, or regional tenant isolated within single-deployment architecture.")
    public ResponseEntity<Tenant> provisionTenant(
            @RequestParam String tenantCode,
            @RequestParam String tenantName,
            @RequestParam String domainName,
            @RequestParam(required = false) String countryCode,
            @RequestParam(required = false) String currencyCode) {
        return ResponseEntity.ok(tenantService.provisionTenant(tenantCode, tenantName, domainName, countryCode, currencyCode));
    }

    @PostMapping("/branding")
    @Operation(summary = "Update White-Label Tenant Branding", description = "Configures custom primary/secondary theme colors, logo URLs, and custom CSS for white-label web and mobile apps.")
    public ResponseEntity<TenantBranding> updateBranding(
            @RequestParam UUID tenantId,
            @RequestParam(required = false) String primaryColor,
            @RequestParam(required = false) String secondaryColor,
            @RequestParam(required = false) String logoUrl,
            @RequestBody(required = false) String customCss) {
        return ResponseEntity.ok(tenantService.updateWhiteLabelBranding(tenantId, primaryColor, secondaryColor, logoUrl, customCss));
    }

    @PostMapping("/subscription")
    @Operation(summary = "Configure Tenant Subscription & Entitlements", description = "Sets SaaS monthly billing fee, maximum order limits, and feature entitlements.")
    public ResponseEntity<TenantSubscription> configureSubscription(
            @RequestParam UUID tenantId,
            @RequestParam(required = false) String planName,
            @RequestParam(required = false) BigDecimal monthlyFee,
            @RequestParam(required = false) Integer maxOrders) {
        return ResponseEntity.ok(tenantService.configureSubscription(tenantId, planName, monthlyFee, maxOrders));
    }
}
