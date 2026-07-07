package com.hyperlofy.backend.analytics.controller;

import com.hyperlofy.backend.analytics.dto.*;
import com.hyperlofy.backend.analytics.entity.AnalyticsSnapshot;
import com.hyperlofy.backend.analytics.service.AnalyticsEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsEngineService analyticsEngineService;

    @GetMapping("/dashboard")
    public ResponseEntity<KPIReport> getKPIReport() {
        log.info("REST request for KPI Dashboard Report.");
        return ResponseEntity.ok(analyticsEngineService.getKPIReport());
    }

    @GetMapping("/orders")
    public ResponseEntity<KPIReport> getOrderAnalytics() {
        log.info("REST request for Order analytical report.");
        return ResponseEntity.ok(analyticsEngineService.getKPIReport());
    }

    @GetMapping("/revenue")
    public ResponseEntity<RevenueReport> getRevenueAnalytics() {
        log.info("REST request for Revenue analytical report.");
        return ResponseEntity.ok(analyticsEngineService.getRevenueReport());
    }

    @GetMapping("/agents")
    public ResponseEntity<AgentPerformanceReport> getAgentAnalytics() {
        log.info("REST request for Agent Performance report.");
        return ResponseEntity.ok(analyticsEngineService.getAgentPerformanceReport());
    }

    @GetMapping("/customers")
    public ResponseEntity<CustomerRetentionReport> getCustomerAnalytics() {
        log.info("REST request for Customer Retention & Segmentation report.");
        return ResponseEntity.ok(analyticsEngineService.getCustomerRetentionReport());
    }

    @GetMapping("/operations")
    public ResponseEntity<OperationalMetrics> getOperationalAnalytics() {
        log.info("REST request for Operational performance reports.");
        return ResponseEntity.ok(analyticsEngineService.getOperationalMetrics());
    }

    @PostMapping("/snapshot/trigger")
    public ResponseEntity<AnalyticsSnapshot> triggerAggregatedSnapshot() {
        log.info("REST request to compile daily analytics snapshot.");
        return ResponseEntity.ok(analyticsEngineService.generateDailySnapshot());
    }
}
