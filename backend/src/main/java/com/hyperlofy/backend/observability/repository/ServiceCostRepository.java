package com.hyperlofy.backend.observability.repository;

import com.hyperlofy.backend.observability.entity.ServiceCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceCostRepository extends JpaRepository<ServiceCost, UUID> {
    Optional<ServiceCost> findByServiceName(String serviceName);
}
