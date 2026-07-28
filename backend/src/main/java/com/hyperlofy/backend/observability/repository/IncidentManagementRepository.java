package com.hyperlofy.backend.observability.repository;

import com.hyperlofy.backend.observability.entity.IncidentManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IncidentManagementRepository extends JpaRepository<IncidentManagement, UUID> {
    Optional<IncidentManagement> findByIncidentCode(String incidentCode);
}
