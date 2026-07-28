package com.hyperlofy.backend.observability.repository;

import com.hyperlofy.backend.observability.entity.RunbookExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RunbookExecutionRepository extends JpaRepository<RunbookExecution, UUID> {
    List<RunbookExecution> findByTargetService(String targetService);
}
