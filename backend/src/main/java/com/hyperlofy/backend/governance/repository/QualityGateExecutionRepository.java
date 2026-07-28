package com.hyperlofy.backend.governance.repository;

import com.hyperlofy.backend.governance.entity.QualityGateExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QualityGateExecutionRepository extends JpaRepository<QualityGateExecution, UUID> {
    Optional<QualityGateExecution> findByExecutionCode(String executionCode);
}
