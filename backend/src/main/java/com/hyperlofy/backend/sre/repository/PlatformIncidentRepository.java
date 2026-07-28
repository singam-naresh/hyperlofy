package com.hyperlofy.backend.sre.repository;

import com.hyperlofy.backend.sre.entity.PlatformIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlatformIncidentRepository extends JpaRepository<PlatformIncident, UUID> {
    Optional<PlatformIncident> findByIncidentNumber(String incidentNumber);
    List<PlatformIncident> findByStatusOrderByCreatedAtDesc(String status);
}
