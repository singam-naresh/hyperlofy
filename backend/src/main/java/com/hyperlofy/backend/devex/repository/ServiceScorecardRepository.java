package com.hyperlofy.backend.devex.repository;

import com.hyperlofy.backend.devex.entity.ServiceScorecard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceScorecardRepository extends JpaRepository<ServiceScorecard, UUID> {
    Optional<ServiceScorecard> findByServiceName(String serviceName);
}
