package com.hyperlofy.backend.workflow.repository;

import com.hyperlofy.backend.workflow.entity.WorkflowInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkflowInstanceRepository extends JpaRepository<WorkflowInstance, UUID> {
    Optional<WorkflowInstance> findByInstanceRef(String instanceRef);
    List<WorkflowInstance> findByCurrentState(String currentState);
    List<WorkflowInstance> findByInitiatorUserId(UUID initiatorUserId);
    List<WorkflowInstance> findByTenantId(UUID tenantId);
}
