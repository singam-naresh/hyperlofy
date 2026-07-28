package com.hyperlofy.backend.workflow.repository;

import com.hyperlofy.backend.workflow.entity.WorkflowTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkflowTaskRepository extends JpaRepository<WorkflowTask, UUID> {
    List<WorkflowTask> findByInstance_Id(UUID instanceId);
    List<WorkflowTask> findByAssigneeUserId(UUID assigneeUserId);
    List<WorkflowTask> findByCandidateGroupAndStatus(String candidateGroup, String status);
    List<WorkflowTask> findByStatus(String status);
}
