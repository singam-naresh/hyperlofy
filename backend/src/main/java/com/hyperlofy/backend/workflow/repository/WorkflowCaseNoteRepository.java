package com.hyperlofy.backend.workflow.repository;

import com.hyperlofy.backend.workflow.entity.WorkflowCaseNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkflowCaseNoteRepository extends JpaRepository<WorkflowCaseNote, UUID> {
    List<WorkflowCaseNote> findByWorkflowCase_IdOrderByCreatedAtAsc(UUID caseId);
}
