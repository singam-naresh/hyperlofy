package com.hyperlofy.backend.sre.repository;

import com.hyperlofy.backend.sre.entity.PlatformHealth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlatformHealthRepository extends JpaRepository<PlatformHealth, UUID> {
    Optional<PlatformHealth> findByServiceName(String serviceName);
}
