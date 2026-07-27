package com.hyperlofy.backend.platform.controller;

import com.hyperlofy.backend.platform.entity.FeatureFlag;
import com.hyperlofy.backend.platform.service.PlatformAdministrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/platform/feature-flags")
@RequiredArgsConstructor
@Tag(name = "Platform Feature Flag API", description = "Endpoints for dynamic feature flag toggles (coupons, wallet, AI recommendations, promotions)")
public class FeatureFlagController {

    private final PlatformAdministrationService platformService;

    @GetMapping
    @Operation(summary = "List Feature Flags", description = "Retrieves all dynamic feature flags.")
    public ResponseEntity<List<FeatureFlag>> getFeatureFlags() {
        return ResponseEntity.ok(platformService.getFeatureFlags());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Toggle Feature Flag", description = "Enables or disables a dynamic feature flag.")
    public ResponseEntity<FeatureFlag> toggleFeatureFlag(
            @RequestParam String key,
            @RequestParam boolean enabled) {

        return ResponseEntity.ok(platformService.toggleFeatureFlag(key, enabled));
    }
}
