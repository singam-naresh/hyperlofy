package com.hyperlofy.backend.workflow.repository;

import com.hyperlofy.backend.workflow.entity.WorkflowForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkflowFormRepository extends JpaRepository<WorkflowForm, UUID> {
    Optional<WorkflowForm> findByFormKey(String formKey);
}
