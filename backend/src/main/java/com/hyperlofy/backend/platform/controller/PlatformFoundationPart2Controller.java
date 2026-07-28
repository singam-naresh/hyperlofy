package com.hyperlofy.backend.platform.controller;

import com.hyperlofy.backend.platform.entity.FeatureFlag;
import com.hyperlofy.backend.platform.entity.SupportedLanguage;
import com.hyperlofy.backend.platform.service.PlatformFoundationPart2Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/platform")
@RequiredArgsConstructor
@Tag(name = "Platform Foundation Part 2 API", description = "Endpoints for internationalization (i18n), runtime feature flags, and secret rotation status")
public class PlatformFoundationPart2Controller {

    private final PlatformFoundationPart2Service platformService;

    @GetMapping("/languages")
    @Operation(summary = "Get Supported Languages", description = "Retrieves active platform languages for i18n localization.")
    public ResponseEntity<List<SupportedLanguage>> getSupportedLanguages() {
        return ResponseEntity.ok(platformService.getActiveSupportedLanguages());
    }

    @GetMapping("/features/{flagKey}")
    @Operation(summary = "Evaluate Feature Flag", description = "Evaluates runtime boolean status of a feature flag.")
    public ResponseEntity<Map<String, Object>> evaluateFeature(@PathVariable String flagKey) {
        boolean enabled = platformService.isFeatureEnabled(flagKey);
        return ResponseEntity.ok(Map.of("flagKey", flagKey, "isEnabled", enabled));
    }

    @PostMapping("/features/{flagKey}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Update Feature Flag", description = "Admin endpoint to update runtime feature flag status and canary rollout percentage.")
    public ResponseEntity<FeatureFlag> updateFeature(
            @PathVariable String flagKey,
            @RequestParam boolean isEnabled,
            @RequestParam int rolloutPercentage) {
        return ResponseEntity.ok(platformService.updateFeatureFlag(flagKey, isEnabled, rolloutPercentage));
    }
}
