package com.hyperlofy.backend.global.repository;

import com.hyperlofy.backend.global.entity.MultiCloudDeployment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MultiCloudDeploymentRepository extends JpaRepository<MultiCloudDeployment, UUID> {
    Optional<MultiCloudDeployment> findByDeploymentCode(String deploymentCode);
}
