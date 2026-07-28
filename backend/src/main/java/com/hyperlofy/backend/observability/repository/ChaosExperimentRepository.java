package com.hyperlofy.backend.observability.repository;

import com.hyperlofy.backend.observability.entity.ChaosExperiment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChaosExperimentRepository extends JpaRepository<ChaosExperiment, UUID> {
    Optional<ChaosExperiment> findByExperimentName(String experimentName);
}
