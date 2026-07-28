package com.hyperlofy.backend.platform.repository;

import com.hyperlofy.backend.platform.entity.BackupJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BackupJobRepository extends JpaRepository<BackupJob, UUID> {
    List<BackupJob> findByTargetSystem(String targetSystem);
}
