package com.hyperlofy.backend.analytics.controller;

import com.hyperlofy.backend.analytics.entity.AnalyticsEvent;
import com.hyperlofy.backend.analytics.entity.AnalyticsKpi;
import com.hyperlofy.backend.analytics.entity.AnalyticsReport;
import com.hyperlofy.backend.analytics.service.AnalyticsEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics/internal")
@RequiredArgsConstructor
@Tag(name = "Analytics Engine Internal Integration API", description = "Endpoints for all Hyperlofy domain engines to ingest telemetry events and publish real-time KPI metrics")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class AnalyticsInternalController {

    private final AnalyticsEngineService analyticsService;

    @PostMapping("/events")
    @Operation(summary = "Ingest Telemetry Analytics Event", description = "Captures event stream telemetry from Orders, Matching, Tracking, Payments, Wallet, Settlement, and Finance engines.")
    public ResponseEntity<AnalyticsEvent> ingestEvent(
            @RequestParam String eventType,
            @RequestParam String sourceService,
            @RequestParam(required = false) UUID entityId,
            @RequestParam(required = false) String payload) {
        return ResponseEntity.ok(analyticsService.ingestEvent(eventType, sourceService, entityId, payload));
    }

    @PostMapping("/kpis/update")
    @Operation(summary = "Update Real-Time Metric KPI", description = "Recalculates GMV, Revenue, Delivery SLA, or Settlement Success Rate metrics.")
    public ResponseEntity<AnalyticsKpi> updateKpi(
            @RequestParam String kpiCode,
            @RequestParam String kpiName,
            @RequestParam BigDecimal metricValue,
            @RequestParam(required = false) String unit) {
        return ResponseEntity.ok(analyticsService.updateKpi(kpiCode, kpiName, metricValue, unit));
    }

    @PostMapping("/reports/generate")
    @Operation(summary = "Generate Business Report", description = "Generates scheduled operational, financial, or executive CSV/PDF reports.")
    public ResponseEntity<AnalyticsReport> generateReport(
            @RequestParam String reportName,
            @RequestParam String reportType,
            @RequestParam(required = false) String format,
            @RequestParam String createdBy) {
        return ResponseEntity.ok(analyticsService.generateScheduledReport(reportName, reportType, format, createdBy));
    }
}
