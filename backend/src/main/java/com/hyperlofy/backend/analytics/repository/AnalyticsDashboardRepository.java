package com.hyperlofy.backend.analytics.repository;

import com.hyperlofy.backend.analytics.entity.AnalyticsDashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnalyticsDashboardRepository extends JpaRepository<AnalyticsDashboard, UUID> {
    Optional<AnalyticsDashboard> findByDashboardKey(String dashboardKey);
}
