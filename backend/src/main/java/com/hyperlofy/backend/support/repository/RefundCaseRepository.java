package com.hyperlofy.backend.support.repository;

import com.hyperlofy.backend.support.entity.RefundCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefundCaseRepository extends JpaRepository<RefundCase, UUID> {
    Optional<RefundCase> findByRefundCode(String refundCode);
    Optional<RefundCase> findByTicket_Id(UUID ticketId);
}
