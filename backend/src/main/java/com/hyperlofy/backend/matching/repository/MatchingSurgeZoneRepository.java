package com.hyperlofy.backend.matching.repository;

import com.hyperlofy.backend.matching.entity.MatchingSurgeZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatchingSurgeZoneRepository extends JpaRepository<MatchingSurgeZone, UUID> {
    Optional<MatchingSurgeZone> findByZoneName(String zoneName);
}
