package com.hyperlofy.backend.workflow.repository;

import com.hyperlofy.backend.workflow.entity.WorkflowHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkflowHistoryRepository extends JpaRepository<WorkflowHistory, UUID> {
    List<WorkflowHistory> findByInstance_IdOrderByCreatedAtAsc(UUID instanceId);
}
