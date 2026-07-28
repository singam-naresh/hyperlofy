package com.hyperlofy.backend.platform.repository;

import com.hyperlofy.backend.platform.entity.DrRunbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DrRunbookRepository extends JpaRepository<DrRunbook, UUID> {
    Optional<DrRunbook> findByRunbookCode(String runbookCode);
}
