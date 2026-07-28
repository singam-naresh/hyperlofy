package com.hyperlofy.backend.platform.repository;

import com.hyperlofy.backend.platform.entity.IncidentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IncidentRecordRepository extends JpaRepository<IncidentRecord, UUID> {
    Optional<IncidentRecord> findByIncidentCode(String incidentCode);
}
