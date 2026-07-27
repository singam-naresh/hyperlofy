package com.hyperlofy.backend.platform.controller;

import com.hyperlofy.backend.platform.entity.PlatformConfiguration;
import com.hyperlofy.backend.platform.service.PlatformAdministrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/platform/configurations")
@RequiredArgsConstructor
@Tag(name = "Platform Configuration API", description = "Endpoints for database-driven system settings (commission rates, delivery radius, tax rate, surge multiplier)")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class PlatformConfigurationController {

    private final PlatformAdministrationService platformService;

    @GetMapping
    @Operation(summary = "List Platform Configurations", description = "Retrieves all database-driven platform configuration key-values.")
    public ResponseEntity<List<PlatformConfiguration>> getConfigurations() {
        return ResponseEntity.ok(platformService.getConfigurations());
    }

    @PostMapping
    @Operation(summary = "Update Platform Configuration", description = "Updates a database-driven system setting.")
    public ResponseEntity<PlatformConfiguration> updateConfiguration(
            @RequestParam String key,
            @RequestParam String value) {

        return ResponseEntity.ok(platformService.updateConfiguration(key, value));
    }
}
