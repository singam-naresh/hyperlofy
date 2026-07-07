package com.hyperlofy.backend.order.repository;

import com.hyperlofy.backend.order.entity.AssignmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssignmentHistoryRepository extends JpaRepository<AssignmentHistory, UUID> {
    List<AssignmentHistory> findByOrderId(UUID orderId);
    List<AssignmentHistory> findByAgentId(UUID agentId);
}
