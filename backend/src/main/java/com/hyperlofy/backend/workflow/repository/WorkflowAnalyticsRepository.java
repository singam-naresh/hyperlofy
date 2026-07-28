package com.hyperlofy.backend.workflow.repository;

import com.hyperlofy.backend.workflow.entity.WorkflowAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkflowAnalyticsRepository extends JpaRepository<WorkflowAnalytics, UUID> {
    List<WorkflowAnalytics> findByWorkflowType(String workflowType);
    Optional<WorkflowAnalytics> findByWorkflowTypeAndPeriodDate(String workflowType, LocalDate periodDate);
}
