package com.hyperlofy.backend.global.repository;

import com.hyperlofy.backend.global.entity.AvailabilityZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AvailabilityZoneRepository extends JpaRepository<AvailabilityZone, UUID> {
    Optional<AvailabilityZone> findByZoneCode(String zoneCode);
    List<AvailabilityZone> findByRegion_Id(UUID regionId);
}
