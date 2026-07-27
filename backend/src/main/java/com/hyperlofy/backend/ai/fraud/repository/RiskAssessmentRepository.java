package com.hyperlofy.backend.ai.fraud.repository;

import com.hyperlofy.backend.ai.fraud.entity.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, UUID> {
    List<RiskAssessment> findByTargetIdOrderByCreatedAtDesc(UUID targetId);
    List<RiskAssessment> findByRiskLevel(String riskLevel);
}
