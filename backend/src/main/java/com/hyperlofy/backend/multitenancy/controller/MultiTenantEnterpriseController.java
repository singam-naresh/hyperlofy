package com.hyperlofy.backend.multitenancy.controller;

import com.hyperlofy.backend.multitenancy.entity.TenantDataResidency;
import com.hyperlofy.backend.multitenancy.entity.TenantDirectorySync;
import com.hyperlofy.backend.multitenancy.entity.TenantIdentityProvider;
import com.hyperlofy.backend.multitenancy.entity.TenantLicenseAllocation;
import com.hyperlofy.backend.multitenancy.service.MultiTenantEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenant/enterprise")
@RequiredArgsConstructor
@Tag(name = "Multi-Tenancy SaaS Enterprise Addendum API", description = "Endpoints for SAML2/OIDC Identity Provider mapping, SCIM 2.0 user directory sync, seat license allocations, and regional data residency compliance")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class MultiTenantEnterpriseController {

    private final MultiTenantEnterpriseService enterpriseService;

    @PostMapping("/identity-provider")
    @Operation(summary = "Configure SAML2/OIDC Enterprise IdP", description = "Maps enterprise single sign-on Identity Provider (Okta, Azure AD, Ping) to specific tenant domain.")
    public ResponseEntity<TenantIdentityProvider> configureIdP(
            @RequestParam UUID tenantId,
            @RequestParam String providerName,
            @RequestParam(required = false) String providerType,
            @RequestParam String issuerUrl,
            @RequestParam String clientId) {
        return ResponseEntity.ok(enterpriseService.configureIdentityProvider(tenantId, providerName, providerType, issuerUrl, clientId));
    }

    @PostMapping("/scim/sync")
    @Operation(summary = "Trigger SCIM 2.0 User Directory Sync", description = "Synchronizes corporate user directories and group memberships in real-time via SCIM protocol.")
    public ResponseEntity<TenantDirectorySync> triggerScimSync(
            @RequestParam UUID tenantId,
            @RequestParam(required = false) String syncSource,
            @RequestParam(required = false) Integer totalUsersSynced) {
        return ResponseEntity.ok(enterpriseService.triggerScimSync(tenantId, syncSource, totalUsersSynced));
    }

    @PostMapping("/license")
    @Operation(summary = "Allocate Enterprise Seat Licenses", description = "Allocates licensed user seats and admin entitlements per tenant subscription tier.")
    public ResponseEntity<TenantLicenseAllocation> allocateLicenses(
            @RequestParam UUID tenantId,
            @RequestParam String licenseType,
            @RequestParam(required = false) Integer totalSeats,
            @RequestParam(required = false) Integer allocatedSeats) {
        return ResponseEntity.ok(enterpriseService.allocateLicenses(tenantId, licenseType, totalSeats, allocatedSeats));
    }

    @PostMapping("/data-residency")
    @Operation(summary = "Configure Regional Data Residency & Compliance", description = "Enforces regional cloud data residency boundaries (GDPR, SOC2, HIPAA) and KMS encryption keys.")
    public ResponseEntity<TenantDataResidency> configureDataResidency(
            @RequestParam UUID tenantId,
            @RequestParam(required = false) String dataRegion,
            @RequestParam(required = false) String complianceStandard,
            @RequestParam(required = false) String encryptionKeyArn) {
        return ResponseEntity.ok(enterpriseService.configureDataResidency(tenantId, dataRegion, complianceStandard, encryptionKeyArn));
    }
}
