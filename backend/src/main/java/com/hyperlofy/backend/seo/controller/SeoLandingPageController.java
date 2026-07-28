package com.hyperlofy.backend.seo.controller;

import com.hyperlofy.backend.seo.entity.SeoLandingPage;
import com.hyperlofy.backend.seo.service.EnterpriseSeoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seo/landing-pages")
@RequiredArgsConstructor
@Tag(name = "Programmatic Landing Page Generator API", description = "Programmatic SEO landing page generation for city-category combinations and seasonal festival campaigns")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class SeoLandingPageController {

    private final EnterpriseSeoService seoService;

    @PostMapping
    @Operation(summary = "Create Programmatic Landing Page", description = "Generates programmatic SEO landing page for City-Category combinations.")
    public ResponseEntity<SeoLandingPage> create(
            @RequestParam String landingCode,
            @RequestParam String cityName,
            @RequestParam String categoryName,
            @RequestParam String keyword,
            @RequestParam String path,
            @RequestParam(required = false) UUID tenantId) {
        return ResponseEntity.ok(seoService.createLandingPage(landingCode, cityName, categoryName, keyword, path, tenantId));
    }
}
