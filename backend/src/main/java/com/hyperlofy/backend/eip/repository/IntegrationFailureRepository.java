package com.hyperlofy.backend.eip.repository;

import com.hyperlofy.backend.eip.entity.IntegrationFailure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IntegrationFailureRepository extends JpaRepository<IntegrationFailure, UUID> {
    List<IntegrationFailure> findByConnectorId(UUID connectorId);
}
