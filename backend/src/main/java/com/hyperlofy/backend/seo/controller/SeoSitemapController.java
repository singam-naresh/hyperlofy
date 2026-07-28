package com.hyperlofy.backend.seo.controller;

import com.hyperlofy.backend.seo.entity.SeoSitemap;
import com.hyperlofy.backend.seo.service.EnterpriseSeoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seo/sitemaps")
@RequiredArgsConstructor
@Tag(name = "XML Sitemap Management API", description = "Automatic regeneration of XML sitemaps for products, merchants, categories, and brands")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class SeoSitemapController {

    private final EnterpriseSeoService seoService;

    @GetMapping
    @Operation(summary = "List Active XML Sitemaps", description = "Returns active XML sitemaps, total indexed URLs, and file paths.")
    public ResponseEntity<List<SeoSitemap>> getAll() {
        return ResponseEntity.ok(seoService.getAllSitemaps());
    }

    @PostMapping("/regenerate")
    @Operation(summary = "Regenerate XML Sitemap", description = "Triggers incremental XML sitemap generation for products, merchants, or categories.")
    public ResponseEntity<SeoSitemap> regenerate(
            @RequestParam String sitemapCode,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String filePath,
            @RequestParam(required = false) Integer totalUrls,
            @RequestParam(required = false) UUID tenantId) {
        return ResponseEntity.ok(seoService.regenerateSitemap(sitemapCode, type, filePath, totalUrls, tenantId));
    }
}
