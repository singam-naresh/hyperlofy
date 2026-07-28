package com.hyperlofy.backend.data.repository;

import com.hyperlofy.backend.data.entity.DataDriftReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DataDriftReportRepository extends JpaRepository<DataDriftReport, UUID> {
    List<DataDriftReport> findByModelId(String modelId);
}
