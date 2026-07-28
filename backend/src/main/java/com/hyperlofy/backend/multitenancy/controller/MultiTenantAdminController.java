package com.hyperlofy.backend.multitenancy.controller;

import com.hyperlofy.backend.multitenancy.entity.Tenant;
import com.hyperlofy.backend.multitenancy.service.MultiTenantPlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tenant/admin")
@RequiredArgsConstructor
@Tag(name = "Multi-Tenancy & SaaS Platform Admin API", description = "Endpoints for Principal Multi-Tenant SaaS Architects to inspect active global tenant domains, brands, and franchises")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class MultiTenantAdminController {

    private final MultiTenantPlatformService tenantService;

    @GetMapping("/tenants")
    @Operation(summary = "Get Active Global SaaS Tenants", description = "Returns active tenants, country/currency regionalizations, and custom domain mappings.")
    public ResponseEntity<List<Tenant>> getTenants() {
        return ResponseEntity.ok(tenantService.getAllActiveTenants());
    }
}
