package com.hyperlofy.backend.analytics.repository;

import com.hyperlofy.backend.analytics.entity.AnalyticsKpi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnalyticsKpiRepository extends JpaRepository<AnalyticsKpi, UUID> {
    Optional<AnalyticsKpi> findByKpiCode(String kpiCode);
}
