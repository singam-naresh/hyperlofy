package com.hyperlofy.backend.platform.repository;

import com.hyperlofy.backend.platform.entity.ExternalIntegration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExternalIntegrationRepository extends JpaRepository<ExternalIntegration, UUID> {
    Optional<ExternalIntegration> findByProviderName(String providerName);
}
