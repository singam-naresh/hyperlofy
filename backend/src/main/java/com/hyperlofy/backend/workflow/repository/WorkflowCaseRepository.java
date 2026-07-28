package com.hyperlofy.backend.workflow.repository;

import com.hyperlofy.backend.workflow.entity.WorkflowCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkflowCaseRepository extends JpaRepository<WorkflowCase, UUID> {
    Optional<WorkflowCase> findByCaseRef(String caseRef);
    List<WorkflowCase> findByTenantIdAndStatus(UUID tenantId, String status);
    List<WorkflowCase> findByCaseType(String caseType);
}
