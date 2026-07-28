package com.hyperlofy.backend.analytics.controller;

import com.hyperlofy.backend.analytics.entity.AnalyticsAiInsight;
import com.hyperlofy.backend.analytics.entity.AnalyticsAnomaly;
import com.hyperlofy.backend.analytics.entity.AnalyticsPrediction;
import com.hyperlofy.backend.analytics.entity.AnalyticsScorecard;
import com.hyperlofy.backend.analytics.service.AnalyticsEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics/enterprise")
@RequiredArgsConstructor
@Tag(name = "Analytics Platform Enterprise Addendum API", description = "Endpoints for Predictive Analytics, AI Business Insights, Real-Time Metric Anomaly Detection, and Executive C-Suite Scorecards")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class AnalyticsEnterpriseController {

    private final AnalyticsEnterpriseService enterpriseService;

    @PostMapping("/predict")
    @Operation(summary = "Generate Machine Learning Demand Forecast", description = "Forecasts order volume, demand spikes, delivery SLA, or revenue projections.")
    public ResponseEntity<AnalyticsPrediction> generatePrediction(
            @RequestParam String target,
            @RequestParam BigDecimal predictedValue,
            @RequestParam(required = false) BigDecimal confidence,
            @RequestParam(required = false) String horizon) {
        return ResponseEntity.ok(enterpriseService.generatePrediction(target, predictedValue, confidence, horizon));
    }

    @PostMapping("/anomaly/detect")
    @Operation(summary = "Trigger Anomaly Detection Event", description = "Logs real-time metric anomalies for revenue drops, settlement failures, or refund spikes.")
    public ResponseEntity<AnalyticsAnomaly> detectAnomaly(
            @RequestParam String metricCode,
            @RequestParam String anomalyType,
            @RequestParam(required = false) String severity,
            @RequestParam BigDecimal baseline,
            @RequestParam BigDecimal observed) {
        return ResponseEntity.ok(enterpriseService.detectAnomaly(metricCode, anomalyType, severity, baseline, observed));
    }

    @PostMapping("/insights/generate")
    @Operation(summary = "Publish AI Business Insight", description = "Generates growth opportunities, operational recommendations, or cost optimization suggestions.")
    public ResponseEntity<AnalyticsAiInsight> generateInsight(
            @RequestParam String category,
            @RequestParam String title,
            @RequestParam String recommendation,
            @RequestParam(required = false) String impactScore) {
        return ResponseEntity.ok(enterpriseService.generateBusinessInsight(category, title, recommendation, impactScore));
    }

    @GetMapping("/anomalies/open")
    @Operation(summary = "Get Open Metric Anomalies", description = "Returns active revenue drop, settlement failure, or refund spike anomalies for SRE and Ops teams.")
    public ResponseEntity<List<AnalyticsAnomaly>> getOpenAnomalies() {
        return ResponseEntity.ok(enterpriseService.getOpenAnomalies());
    }

    @GetMapping("/scorecard/{role}")
    @Operation(summary = "Get C-Suite Executive Scorecard", description = "Returns CEO, COO, or CFO scorecard with overall platform performance grades.")
    public ResponseEntity<AnalyticsScorecard> getScorecard(@PathVariable String role) {
        return ResponseEntity.ok(enterpriseService.getExecutiveScorecard(role));
    }
}
