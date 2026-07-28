package com.hyperlofy.backend.platform.repository;

import com.hyperlofy.backend.platform.entity.ChaosExperiment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChaosExperimentRepository extends JpaRepository<ChaosExperiment, UUID> {
    Optional<ChaosExperiment> findByExperimentCode(String experimentCode);
}
