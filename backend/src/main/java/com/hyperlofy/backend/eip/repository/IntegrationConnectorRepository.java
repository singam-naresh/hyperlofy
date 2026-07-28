package com.hyperlofy.backend.eip.repository;

import com.hyperlofy.backend.eip.entity.IntegrationConnector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IntegrationConnectorRepository extends JpaRepository<IntegrationConnector, UUID> {
    Optional<IntegrationConnector> findByConnectorCode(String connectorCode);
}
