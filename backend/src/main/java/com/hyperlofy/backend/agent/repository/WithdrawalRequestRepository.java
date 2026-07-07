package com.hyperlofy.backend.agent.repository;

import com.hyperlofy.backend.agent.entity.WithdrawalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, UUID> {
    List<WithdrawalRequest> findByAgentIdOrderByCreatedAtDesc(UUID agentId);
    List<WithdrawalRequest> findByStatus(String status);
}
