package com.hyperlofy.backend.eip.repository;

import com.hyperlofy.backend.eip.entity.IntegrationJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IntegrationJobRepository extends JpaRepository<IntegrationJob, UUID> {
    List<IntegrationJob> findByConnectorId(UUID connectorId);
}
