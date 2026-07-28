package com.hyperlofy.backend.devex.repository;

import com.hyperlofy.backend.devex.entity.ServiceCatalogItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceCatalogItemRepository extends JpaRepository<ServiceCatalogItem, UUID> {
    Optional<ServiceCatalogItem> findByServiceName(String serviceName);
}
