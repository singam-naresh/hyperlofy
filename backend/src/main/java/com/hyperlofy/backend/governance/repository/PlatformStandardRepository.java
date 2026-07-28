package com.hyperlofy.backend.governance.repository;

import com.hyperlofy.backend.governance.entity.PlatformStandard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlatformStandardRepository extends JpaRepository<PlatformStandard, UUID> {
    Optional<PlatformStandard> findByStandardKey(String standardKey);
}
