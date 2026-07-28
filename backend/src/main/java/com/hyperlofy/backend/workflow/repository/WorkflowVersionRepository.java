package com.hyperlofy.backend.workflow.repository;

import com.hyperlofy.backend.workflow.entity.WorkflowVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkflowVersionRepository extends JpaRepository<WorkflowVersion, UUID> {
    List<WorkflowVersion> findByDefinition_Id(UUID definitionId);
    Optional<WorkflowVersion> findByDefinition_IdAndVersionStatus(UUID definitionId, String versionStatus);
}
