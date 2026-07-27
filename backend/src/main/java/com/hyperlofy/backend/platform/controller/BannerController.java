package com.hyperlofy.backend.platform.controller;

import com.hyperlofy.backend.platform.entity.Banner;
import com.hyperlofy.backend.platform.service.PlatformAdministrationService;
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
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
@Tag(name = "Platform Campaign & Banner API", description = "Endpoints for marketing banners, homepage campaigns, and priority ordering")
public class BannerController {

    private final PlatformAdministrationService platformService;

    @GetMapping
    @Operation(summary = "List Banners", description = "Retrieves active marketing banners.")
    public ResponseEntity<List<Banner>> getAllBanners() {
        return ResponseEntity.ok(platformService.getAllBanners());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Create Banner", description = "Creates a new promotional marketing banner.")
    public ResponseEntity<Banner> createBanner(@Valid @RequestBody Banner banner) {
        return ResponseEntity.ok(platformService.createBanner(banner));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Activate/Deactivate Banner", description = "Toggles banner active status.")
    public ResponseEntity<Banner> setBannerActive(@PathVariable UUID id, @RequestParam boolean active) {
        return ResponseEntity.ok(platformService.setBannerActive(id, active));
    }
}
