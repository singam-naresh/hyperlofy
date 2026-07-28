package com.hyperlofy.backend.support.repository;

import com.hyperlofy.backend.support.entity.ReplacementCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReplacementCaseRepository extends JpaRepository<ReplacementCase, UUID> {
    Optional<ReplacementCase> findByReplacementCode(String replacementCode);
    Optional<ReplacementCase> findByTicket_Id(UUID ticketId);
}
