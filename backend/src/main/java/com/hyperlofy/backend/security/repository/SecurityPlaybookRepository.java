package com.hyperlofy.backend.security.repository;

import com.hyperlofy.backend.security.entity.SecurityPlaybook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SecurityPlaybookRepository extends JpaRepository<SecurityPlaybook, UUID> {
    Optional<SecurityPlaybook> findByPlaybookCode(String playbookCode);
}
