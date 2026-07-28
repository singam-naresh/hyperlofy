package com.hyperlofy.backend.global.repository;

import com.hyperlofy.backend.global.entity.BackupExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BackupExecutionRepository extends JpaRepository<BackupExecution, UUID> {
    Optional<BackupExecution> findByBackupCode(String backupCode);
    List<BackupExecution> findByRegionCode(String regionCode);
}
