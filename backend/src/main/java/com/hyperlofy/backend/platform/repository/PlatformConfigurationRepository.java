package com.hyperlofy.backend.platform.repository;

import com.hyperlofy.backend.platform.entity.PlatformConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlatformConfigurationRepository extends JpaRepository<PlatformConfiguration, UUID> {
    Optional<PlatformConfiguration> findByConfigKey(String configKey);
}
