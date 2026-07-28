package com.hyperlofy.backend.global.repository;

import com.hyperlofy.backend.global.entity.GlobalTrafficOptimization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GlobalTrafficOptimizationRepository extends JpaRepository<GlobalTrafficOptimization, UUID> {
    Optional<GlobalTrafficOptimization> findByOptimizationCode(String optimizationCode);
}
