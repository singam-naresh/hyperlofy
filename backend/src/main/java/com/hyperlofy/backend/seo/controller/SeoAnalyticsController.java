package com.hyperlofy.backend.seo.controller;

import com.hyperlofy.backend.seo.entity.SeoKeywordRanking;
import com.hyperlofy.backend.seo.service.EnterpriseSeoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seo/analytics")
@RequiredArgsConstructor
@Tag(name = "Search Analytics & Growth Dashboard API", description = "Query organic search impressions, keyword rankings, organic CTR, and index coverage metrics")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class SeoAnalyticsController {

    private final EnterpriseSeoService seoService;

    @GetMapping("/keywords")
    @Operation(summary = "Get Keyword Ranking Analytics", description = "Returns monthly search volumes, SERP position ranks, and organic click-through rates (CTR).")
    public ResponseEntity<List<SeoKeywordRanking>> getKeywords() {
        return ResponseEntity.ok(seoService.getKeywordRankings());
    }
}
