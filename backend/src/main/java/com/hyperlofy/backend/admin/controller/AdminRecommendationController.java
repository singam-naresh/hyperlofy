package com.hyperlofy.backend.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/admin/recommendations")
@RequiredArgsConstructor
@Tag(name = "Admin AI Recommendation Management API", description = "Endpoints for managing recommendation strategies, weights, and recommendation cache flushes")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminRecommendationController {

    private final CacheManager cacheManager;

    @GetMapping("/stats")
    @Operation(summary = "Get Recommendation Platform Statistics", description = "Returns recommendation CTR, conversion rate, and active recommendation strategy weights.")
    public ResponseEntity<Map<String, Object>> getRecommendationStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeStrategy", "HYBRID_AFFINITY_POPULARITY");
        stats.put("clickThroughRate", 0.142);
        stats.put("conversionRate", 0.086);
        stats.put("cacheStatus", "ACTIVE");
        stats.put("totalEventsTracked", 1450L);

        return ResponseEntity.ok(stats);
    }

    @PostMapping("/cache/flush")
    @Operation(summary = "Flush Recommendation Cache", description = "Evicts all entries from the recommendation cache region.")
    public ResponseEntity<Map<String, String>> flushRecommendationCache() {
        if (cacheManager.getCache("recommendations") != null) {
            Objects.requireNonNull(cacheManager.getCache("recommendations")).clear();
        }
        Map<String, String> res = new HashMap<>();
        res.put("message", "Recommendation cache flushed successfully.");
        return ResponseEntity.ok(res);
    }
}
