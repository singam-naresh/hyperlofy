package com.hyperlofy.backend.platform.repository;

import com.hyperlofy.backend.platform.entity.PerformanceMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PerformanceMetricRepository extends JpaRepository<PerformanceMetric, UUID> {
    List<PerformanceMetric> findByServiceName(String serviceName);
}
