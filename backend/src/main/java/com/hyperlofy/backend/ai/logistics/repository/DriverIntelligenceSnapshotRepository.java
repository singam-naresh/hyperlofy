package com.hyperlofy.backend.ai.logistics.repository;

import com.hyperlofy.backend.ai.logistics.entity.DriverIntelligenceSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverIntelligenceSnapshotRepository extends JpaRepository<DriverIntelligenceSnapshot, UUID> {
    Optional<DriverIntelligenceSnapshot> findByDriverId(UUID driverId);
}
