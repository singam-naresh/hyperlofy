package com.hyperlofy.backend.support.repository;

import com.hyperlofy.backend.support.entity.ReturnCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReturnCaseRepository extends JpaRepository<ReturnCase, UUID> {
    Optional<ReturnCase> findByReturnCode(String returnCode);
    Optional<ReturnCase> findByTicket_Id(UUID ticketId);
}
