package com.hyperlofy.backend.governance.repository;

import com.hyperlofy.backend.governance.entity.ArchitectureDecisionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArchitectureDecisionRecordRepository extends JpaRepository<ArchitectureDecisionRecord, UUID> {
    Optional<ArchitectureDecisionRecord> findByAdrCode(String adrCode);
}
