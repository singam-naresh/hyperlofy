package com.hyperlofy.backend.platform.controller;

import com.hyperlofy.backend.platform.entity.CmsPage;
import com.hyperlofy.backend.platform.service.PlatformAdministrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cms")
@RequiredArgsConstructor
@Tag(name = "Platform CMS API", description = "Endpoints for managing system pages (Privacy Policy, Terms & Conditions, FAQ, About Us)")
public class CmsController {

    private final PlatformAdministrationService platformService;

    @GetMapping("/{slug}")
    @Operation(summary = "Get CMS Page by Slug", description = "Retrieves published content for a CMS page (e.g. privacy-policy, terms-and-conditions).")
    public ResponseEntity<CmsPage> getCmsPage(@PathVariable String slug) {
        return ResponseEntity.ok(platformService.getCmsPage(slug));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Save CMS Page", description = "Creates or updates content for a CMS page.")
    public ResponseEntity<CmsPage> saveCmsPage(@Valid @RequestBody CmsPage page) {
        return ResponseEntity.ok(platformService.saveCmsPage(page));
    }
}
