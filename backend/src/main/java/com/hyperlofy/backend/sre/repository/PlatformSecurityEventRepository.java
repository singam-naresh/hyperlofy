package com.hyperlofy.backend.sre.repository;

import com.hyperlofy.backend.sre.entity.PlatformSecurityEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlatformSecurityEventRepository extends JpaRepository<PlatformSecurityEvent, UUID> {
    List<PlatformSecurityEvent> findBySourceComponentOrderByCreatedAtDesc(String sourceComponent);
}
