package com.hyperlofy.backend.global.repository;

import com.hyperlofy.backend.global.entity.DisasterRecoveryPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DisasterRecoveryPlanRepository extends JpaRepository<DisasterRecoveryPlan, UUID> {
    Optional<DisasterRecoveryPlan> findByPlanName(String planName);
}
