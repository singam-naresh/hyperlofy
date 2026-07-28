package com.hyperlofy.backend.workflow.repository;

import com.hyperlofy.backend.workflow.entity.WorkflowEscalationPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkflowEscalationPolicyRepository extends JpaRepository<WorkflowEscalationPolicy, UUID> {
    List<WorkflowEscalationPolicy> findByAppliesToWorkflowTypeAndIsActiveTrue(String workflowType);
    List<WorkflowEscalationPolicy> findByIsActiveTrue();
}
