package com.hyperlofy.backend.admin.controller;

import com.hyperlofy.backend.platform.entity.CitySetting;
import com.hyperlofy.backend.platform.service.CityManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/cities")
@RequiredArgsConstructor
@Tag(name = "Admin Multi-City & Zone Management API", description = "Endpoints for enabling multi-city support, configuring city delivery radius, and service availability")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminCityZoneController {

    private final CityManagementService cityService;

    @PostMapping
    @Operation(summary = "Configure City Settings", description = "Enables a new city, configures operating hours, maximum delivery radius, and active service flags.")
    public ResponseEntity<CitySetting> configureCity(@RequestBody CitySetting citySetting) {
        return ResponseEntity.ok(cityService.configureCity(citySetting));
    }

    @GetMapping
    @Operation(summary = "Get Active Cities", description = "Retrieves all active platform cities and service zone parameters.")
    public ResponseEntity<List<CitySetting>> getAllCities() {
        return ResponseEntity.ok(cityService.getAllActiveCities());
    }
}
