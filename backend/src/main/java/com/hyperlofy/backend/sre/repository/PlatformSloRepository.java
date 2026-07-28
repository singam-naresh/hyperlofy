package com.hyperlofy.backend.sre.repository;

import com.hyperlofy.backend.sre.entity.PlatformSlo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlatformSloRepository extends JpaRepository<PlatformSlo, UUID> {
    Optional<PlatformSlo> findBySloName(String sloName);
}
