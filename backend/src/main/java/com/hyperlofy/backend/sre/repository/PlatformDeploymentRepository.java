package com.hyperlofy.backend.sre.repository;

import com.hyperlofy.backend.sre.entity.PlatformDeployment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlatformDeploymentRepository extends JpaRepository<PlatformDeployment, UUID> {
    Optional<PlatformDeployment> findByDeploymentNumber(String deploymentNumber);
    List<PlatformDeployment> findByServiceNameOrderByCreatedAtDesc(String serviceName);
}
