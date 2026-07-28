package com.hyperlofy.backend.security.repository;

import com.hyperlofy.backend.security.entity.PrivilegedSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrivilegedSessionRepository extends JpaRepository<PrivilegedSession, UUID> {
    List<PrivilegedSession> findByUserId(UUID userId);
}
