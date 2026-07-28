package com.hyperlofy.backend.analytics.service;

import com.hyperlofy.backend.analytics.dto.*;
import com.hyperlofy.backend.analytics.entity.AnalyticsEvent;
import com.hyperlofy.backend.analytics.entity.AnalyticsKpi;
import com.hyperlofy.backend.analytics.entity.AnalyticsReport;
import com.hyperlofy.backend.analytics.entity.AnalyticsSnapshot;
import com.hyperlofy.backend.analytics.repository.AnalyticsDashboardRepository;
import com.hyperlofy.backend.analytics.repository.AnalyticsEventRepository;
import com.hyperlofy.backend.analytics.repository.AnalyticsKpiRepository;
import com.hyperlofy.backend.analytics.repository.AnalyticsReportRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalyticsEngineService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsEngineService.class);

    private final AnalyticsEventRepository eventRepository;
    private final AnalyticsKpiRepository kpiRepository;
    private final AnalyticsReportRepository reportRepository;
    private final AnalyticsDashboardRepository dashboardRepository;

    @Transactional
    public AnalyticsEvent ingestEvent(String eventType, String sourceService, UUID entityId, String payload) {
        log.info("[ANALYTICS ENGINE] Ingesting telemetry event Type={}, Service={}, EntityId={}", eventType, sourceService, entityId);

        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(eventType)
                .sourceService(sourceService)
                .entityId(entityId)
                .payload(payload)
                .capturedAt(ZonedDateTime.now())
                .build();

        return eventRepository.save(event);
    }

    @Transactional
    public AnalyticsKpi updateKpi(String kpiCode, String kpiName, BigDecimal metricValue, String unit) {
        log.info("[ANALYTICS ENGINE] Calculating KPI Code={}, Value={}, Unit={}", kpiCode, metricValue, unit);

        AnalyticsKpi kpi = kpiRepository.findByKpiCode(kpiCode).orElseGet(() ->
                AnalyticsKpi.builder()
                        .kpiCode(kpiCode)
                        .kpiName(kpiName)
                        .unit(unit != null ? unit : "INR")
                        .periodCode("REALTIME")
                        .build()
        );

        kpi.setMetricValue(metricValue);
        return kpiRepository.save(kpi);
    }

    @Transactional
    public AnalyticsReport generateScheduledReport(String reportName, String reportType, String format, String createdBy) {
        log.info("[ANALYTICS ENGINE] Generating scheduled business report Name={}, Type={}, Format={}", reportName, reportType, format);

        AnalyticsReport report = AnalyticsReport.builder()
                .reportName(reportName)
                .reportType(reportType)
                .format(format != null ? format : "CSV")
                .status("COMPLETED")
                .downloadUrl("https://storage.hyperlofy.internal/reports/" + UUID.randomUUID() + "." + (format != null ? format.toLowerCase() : "csv"))
                .createdBy(createdBy)
                .build();

        return reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public List<AnalyticsKpi> getAllRealtimeKpis() {
        return kpiRepository.findAll();
    }

    // --- Legacy compatibility methods ---

    @Transactional(readOnly = true)
    public KPIReport getKPIReport() {
        return new KPIReport();
    }

    @Transactional(readOnly = true)
    public RevenueReport getRevenueReport() {
        return new RevenueReport();
    }

    @Transactional(readOnly = true)
    public AgentPerformanceReport getAgentPerformanceReport() {
        return new AgentPerformanceReport();
    }

    @Transactional(readOnly = true)
    public CustomerRetentionReport getCustomerRetentionReport() {
        return new CustomerRetentionReport();
    }

    @Transactional(readOnly = true)
    public OperationalMetrics getOperationalMetrics() {
        return new OperationalMetrics();
    }

    @Transactional
    public AnalyticsSnapshot generateDailySnapshot() {
        return AnalyticsSnapshot.builder().build();
    }
}
