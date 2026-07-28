package com.hyperlofy.backend.observability.repository;

import com.hyperlofy.backend.observability.entity.ObservabilityDashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ObservabilityDashboardRepository extends JpaRepository<ObservabilityDashboard, UUID> {
    Optional<ObservabilityDashboard> findByDashboardName(String dashboardName);
}
