package com.hyperlofy.backend.platform.repository;

import com.hyperlofy.backend.platform.entity.DrRecoveryMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DrRecoveryMetricRepository extends JpaRepository<DrRecoveryMetric, UUID> {
    Optional<DrRecoveryMetric> findByServiceName(String serviceName);
}
