package com.hyperlofy.backend.eip.repository;

import com.hyperlofy.backend.eip.entity.IntegrationWebhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IntegrationWebhookRepository extends JpaRepository<IntegrationWebhook, UUID> {
    List<IntegrationWebhook> findByConnectorId(UUID connectorId);
}
