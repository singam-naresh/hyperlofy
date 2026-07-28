package com.hyperlofy.backend.eip.repository;

import com.hyperlofy.backend.eip.entity.ConnectorMarketplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConnectorMarketplaceRepository extends JpaRepository<ConnectorMarketplace, UUID> {
    Optional<ConnectorMarketplace> findByTemplateName(String templateName);
}
