package com.hyperlofy.backend.global.repository;

import com.hyperlofy.backend.global.entity.AutonomousRecoveryExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AutonomousRecoveryExecutionRepository extends JpaRepository<AutonomousRecoveryExecution, UUID> {
    Optional<AutonomousRecoveryExecution> findByExecutionCode(String executionCode);
}
