package com.hyperlofy.backend.analytics.controller;

import com.hyperlofy.backend.analytics.entity.AnalyticsKpi;
import com.hyperlofy.backend.analytics.service.AnalyticsEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics/admin")
@RequiredArgsConstructor
@Tag(name = "Analytics Engine Admin API", description = "Endpoints for executive leadership and operations leads to query real-time platform KPIs and executive dashboards")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AnalyticsAdminController {

    private final AnalyticsEngineService analyticsService;

    @GetMapping("/kpis")
    @Operation(summary = "Get Real-Time Executive KPIs", description = "Returns platform GMV, Revenue, Order Volume, Delivery SLA, and Wallet Utilization metrics.")
    public ResponseEntity<List<AnalyticsKpi>> getKpis() {
        return ResponseEntity.ok(analyticsService.getAllRealtimeKpis());
    }
}
