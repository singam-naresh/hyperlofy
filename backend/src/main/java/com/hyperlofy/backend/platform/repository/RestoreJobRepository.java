package com.hyperlofy.backend.platform.repository;

import com.hyperlofy.backend.platform.entity.RestoreJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RestoreJobRepository extends JpaRepository<RestoreJob, UUID> {
    List<RestoreJob> findByRestoreTargetOrderByCreatedAtDesc(String restoreTarget);
}
