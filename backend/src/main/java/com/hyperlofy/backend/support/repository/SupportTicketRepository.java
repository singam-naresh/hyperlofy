package com.hyperlofy.backend.support.repository;

import com.hyperlofy.backend.support.entity.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, UUID> {
    Optional<SupportTicket> findByTicketCode(String ticketCode);
    List<SupportTicket> findByCustomerId(UUID customerId);
    List<SupportTicket> findByAssignedAgentId(UUID assignedAgentId);
    List<SupportTicket> findByStatus(String status);
}
