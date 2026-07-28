package com.hyperlofy.backend.seo.repository;

import com.hyperlofy.backend.seo.entity.SeoAuditReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeoAuditReportRepository extends JpaRepository<SeoAuditReport, UUID> {
    Optional<SeoAuditReport> findByAuditCode(String auditCode);
}
