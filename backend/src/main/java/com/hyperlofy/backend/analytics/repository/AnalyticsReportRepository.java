package com.hyperlofy.backend.analytics.repository;

import com.hyperlofy.backend.analytics.entity.AnalyticsReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnalyticsReportRepository extends JpaRepository<AnalyticsReport, UUID> {
    List<AnalyticsReport> findByReportTypeOrderByCreatedAtDesc(String reportType);
}
