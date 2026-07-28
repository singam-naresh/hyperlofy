package com.hyperlofy.backend.platform.repository;

import com.hyperlofy.backend.platform.entity.LegalHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LegalHoldRepository extends JpaRepository<LegalHold, UUID> {
    Optional<LegalHold> findByCaseId(String caseId);
    List<LegalHold> findByTargetTableAndTargetRecordIdAndIsActiveTrue(String targetTable, String targetRecordId);
}
