package com.hyperlofy.backend.devex.controller;

import com.hyperlofy.backend.devex.entity.ServiceCatalogItem;
import com.hyperlofy.backend.devex.service.DeveloperPlatformService;
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
@RequestMapping("/api/v1/devex/admin")
@RequiredArgsConstructor
@Tag(name = "Developer Platform & API Gateway Admin API", description = "Endpoints for Principal Developer Platform Architects to review IDP service catalog and API gateway throughput statistics")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class DeveloperPlatformAdminController {

    private final DeveloperPlatformService devPlatformService;

    @GetMapping("/services/catalog")
    @Operation(summary = "Get IDP Service Catalog", description = "Returns active microservices, tech stack versions, owner teams, and repository links.")
    public ResponseEntity<List<ServiceCatalogItem>> getCatalog() {
        return ResponseEntity.ok(devPlatformService.getServiceCatalog());
    }
}
