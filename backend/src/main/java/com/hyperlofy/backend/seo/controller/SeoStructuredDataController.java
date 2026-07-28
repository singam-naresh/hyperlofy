package com.hyperlofy.backend.seo.controller;

import com.hyperlofy.backend.seo.entity.SeoStructuredData;
import com.hyperlofy.backend.seo.service.EnterpriseSeoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seo/schema")
@RequiredArgsConstructor
@Tag(name = "Structured Data & Schema.org API", description = "Generate Schema.org JSON-LD structured markup for Products, Merchants, FAQs, and Breadcrumbs")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class SeoStructuredDataController {

    private final EnterpriseSeoService seoService;

    @PostMapping("/generate")
    @Operation(summary = "Generate Schema.org JSON-LD Markup", description = "Generates Schema.org JSON-LD structured markup for specified page.")
    public ResponseEntity<SeoStructuredData> generate(
            @RequestParam UUID pageId,
            @RequestParam(required = false) String schemaType,
            @RequestParam String jsonLdContent,
            @RequestParam(required = false) UUID tenantId) {
        return ResponseEntity.ok(seoService.generateSchema(pageId, schemaType, jsonLdContent, tenantId));
    }
}
