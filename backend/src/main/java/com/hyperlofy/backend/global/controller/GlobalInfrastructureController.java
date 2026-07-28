package com.hyperlofy.backend.global.controller;

import com.hyperlofy.backend.global.entity.GlobalRegion;
import com.hyperlofy.backend.global.service.GlobalInfrastructureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/global")
@RequiredArgsConstructor
@Tag(name = "Global Infrastructure & Multi-Region Platform API", description = "Register global cloud regions (India, Singapore, UAE, Europe, NA, Australia), availability zones, and multi-region health status")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class GlobalInfrastructureController {

    private final GlobalInfrastructureService globalService;

    @PostMapping("/regions")
    @Operation(summary = "Register Global Cloud Region", description = "Registers a new cloud region (ap-south-1 India, ap-southeast-1 Singapore, me-central-1 UAE, eu-central-1 Europe, us-east-1 NA, ap-southeast-2 Australia).")
    public ResponseEntity<GlobalRegion> registerRegion(
            @RequestParam String regionCode,
            @RequestParam String regionName,
            @RequestParam String countryCode,
            @RequestParam(required = false) String cloudProvider,
            @RequestParam(required = false, defaultValue = "false") boolean isPrimary) {
        return ResponseEntity.ok(globalService.registerRegion(regionCode, regionName, countryCode, cloudProvider, isPrimary));
    }

    @GetMapping("/regions")
    @Operation(summary = "List All Global Regions", description = "Returns active global cloud regions, deployment modes (ACTIVE, PASSIVE, DRAINED), and primary cloud providers.")
    public ResponseEntity<List<GlobalRegion>> getRegions() {
        return ResponseEntity.ok(globalService.getAllRegions());
    }

    @GetMapping("/health")
    @Operation(summary = "Get Global Multi-Region Health Summary", description = "Returns health status summary across all cloud regions and availability zones.")
    public ResponseEntity<List<GlobalRegion>> getHealth() {
        return ResponseEntity.ok(globalService.getAllRegions());
    }
}
