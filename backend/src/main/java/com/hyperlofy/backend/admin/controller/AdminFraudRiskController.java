package com.hyperlofy.backend.admin.controller;

import com.hyperlofy.backend.ai.fraud.entity.RiskAssessment;
import com.hyperlofy.backend.ai.fraud.service.FraudRiskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/fraud-risk")
@RequiredArgsConstructor
@Tag(name = "Admin AI Fraud Detection & Risk Intelligence API", description = "Endpoints for platform risk analytics, fraud dashboard, entity risk lookup, and rule controls")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminFraudRiskController {

    private final FraudRiskService fraudRiskService;
    private final CacheManager cacheManager;

    @GetMapping("/dashboard")
    @Operation(summary = "Get Fraud Detection Dashboard", description = "Retrieves real-time platform risk statistics, blocked transactions count, and evaluation latency.")
    public ResponseEntity<Map<String, Object>> getFraudDashboard() {
        return ResponseEntity.ok(fraudRiskService.getPlatformFraudDashboard());
    }

    @GetMapping("/history/{targetId}")
    @Operation(summary = "Get Target Risk History", description = "Retrieves risk assessment history for a specific customer, order, merchant, or driver.")
    public ResponseEntity<List<RiskAssessment>> getTargetRiskHistory(@PathVariable UUID targetId) {
        return ResponseEntity.ok(fraudRiskService.getTargetRiskHistory(targetId));
    }

    @PostMapping("/cache/flush")
    @Operation(summary = "Flush Risk Intelligence Cache", description = "Evicts cached risk assessments and fraud rules from Redis.")
    public ResponseEntity<Map<String, String>> flushRiskCache() {
        if (cacheManager.getCache("risk_assessments") != null) {
            Objects.requireNonNull(cacheManager.getCache("risk_assessments")).clear();
        }
        Map<String, String> res = Map.of("message", "Fraud detection and risk intelligence caches flushed successfully.");
        return ResponseEntity.ok(res);
    }
}
