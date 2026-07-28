package com.hyperlofy.backend.admin.repository;

import com.hyperlofy.backend.admin.entity.AdminWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminWorkflowRepository extends JpaRepository<AdminWorkflow, UUID> {
    Optional<AdminWorkflow> findByWorkflowName(String workflowName);
}
