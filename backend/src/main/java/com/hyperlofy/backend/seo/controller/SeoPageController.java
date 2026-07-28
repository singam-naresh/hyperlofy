package com.hyperlofy.backend.seo.controller;

import com.hyperlofy.backend.seo.entity.SeoAuditReport;
import com.hyperlofy.backend.seo.entity.SeoPage;
import com.hyperlofy.backend.seo.service.EnterpriseSeoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seo/pages")
@RequiredArgsConstructor
@Tag(name = "Technical SEO & Metadata API", description = "Manage dynamic meta titles, descriptions, canonical URLs, Open Graph tags, and execute AI SEO health audits")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class SeoPageController {

    private final EnterpriseSeoService seoService;

    @PostMapping
    @Operation(summary = "Upsert Technical SEO Page Metadata", description = "Upserts meta title, description, canonical URL, and Open Graph tags for marketplace pages.")
    public ResponseEntity<SeoPage> upsert(
            @RequestParam String pageUrl,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) String canonical,
            @RequestParam(required = false) String ogTitle,
            @RequestParam(required = false) String ogImage,
            @RequestParam(required = false) UUID tenantId) {
        return ResponseEntity.ok(seoService.upsertSeoPage(pageUrl, title, description, canonical, ogTitle, ogImage, tenantId));
    }

    @PostMapping("/audit")
    @Operation(summary = "Execute AI Technical SEO Audit", description = "Executes AI-powered technical SEO health audit for specified page URL.")
    public ResponseEntity<SeoAuditReport> audit(
            @RequestParam String auditCode,
            @RequestParam UUID pageId,
            @RequestParam(required = false) String recommendations,
            @RequestParam(required = false) UUID tenantId) {
        return ResponseEntity.ok(seoService.auditPage(auditCode, pageId, recommendations, tenantId));
    }
}
