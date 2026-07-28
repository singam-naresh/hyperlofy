package com.hyperlofy.backend.sre.repository;

import com.hyperlofy.backend.sre.entity.PlatformRunbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlatformRunbookRepository extends JpaRepository<PlatformRunbook, UUID> {
    Optional<PlatformRunbook> findByRunbookName(String runbookName);
}
