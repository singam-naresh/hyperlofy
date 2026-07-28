package com.hyperlofy.backend.platform.repository;

import com.hyperlofy.backend.platform.entity.DrFailoverLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DrFailoverLogRepository extends JpaRepository<DrFailoverLog, UUID> {
    List<DrFailoverLog> findByTargetSystemOrderByCreatedAtDesc(String targetSystem);
}
