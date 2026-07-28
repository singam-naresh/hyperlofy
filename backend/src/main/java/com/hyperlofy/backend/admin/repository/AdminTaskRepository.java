package com.hyperlofy.backend.admin.repository;

import com.hyperlofy.backend.admin.entity.AdminTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminTaskRepository extends JpaRepository<AdminTask, UUID> {
    Optional<AdminTask> findByTaskNumber(String taskNumber);
    List<AdminTask> findByAssignedAgentOrderByCreatedAtDesc(String assignedAgent);
}
