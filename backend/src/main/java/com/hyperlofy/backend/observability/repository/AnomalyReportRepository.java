package com.hyperlofy.backend.observability.repository;

import com.hyperlofy.backend.observability.entity.AnomalyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnomalyReportRepository extends JpaRepository<AnomalyReport, UUID> {
    List<AnomalyReport> findByServiceName(String serviceName);
}
