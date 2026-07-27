package com.hyperlofy.backend.admin.controller;

import com.hyperlofy.backend.admin.service.AdminPlatformService;
import com.hyperlofy.backend.zone.entity.Zone;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/zones")
@RequiredArgsConstructor
@Tag(name = "Admin Zone Administration API", description = "Endpoints for delivery zone management, geo-coverage configuration, and zone status toggles")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminZoneController {

    private final AdminPlatformService adminPlatformService;

    @GetMapping
    @Operation(summary = "List Delivery Zones", description = "Retrieves all configured platform delivery zones.")
    public ResponseEntity<List<Zone>> getZones() {
        return ResponseEntity.ok(adminPlatformService.getZones());
    }

    @PostMapping
    @Operation(summary = "Create Delivery Zone", description = "Creates a new delivery coverage zone.")
    public ResponseEntity<Zone> createZone(@Valid @RequestBody Zone zone) {
        return ResponseEntity.ok(adminPlatformService.createZone(zone));
    }

    @PutMapping("/{zoneId}")
    @Operation(summary = "Update Delivery Zone", description = "Updates details for a delivery zone.")
    public ResponseEntity<Zone> updateZone(@PathVariable UUID zoneId, @Valid @RequestBody Zone zone) {
        return ResponseEntity.ok(adminPlatformService.updateZone(zoneId, zone));
    }

    @PatchMapping("/{zoneId}/activate")
    @Operation(summary = "Activate Zone", description = "Activates a delivery zone.")
    public ResponseEntity<Zone> activateZone(@PathVariable UUID zoneId) {
        return ResponseEntity.ok(adminPlatformService.setZoneActive(zoneId, true));
    }

    @PatchMapping("/{zoneId}/deactivate")
    @Operation(summary = "Deactivate Zone", description = "Deactivates a delivery zone.")
    public ResponseEntity<Zone> deactivateZone(@PathVariable UUID zoneId) {
        return ResponseEntity.ok(adminPlatformService.setZoneActive(zoneId, false));
    }
}
