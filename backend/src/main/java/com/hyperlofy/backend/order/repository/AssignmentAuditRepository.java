package com.hyperlofy.backend.order.repository;

import com.hyperlofy.backend.order.entity.AssignmentAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssignmentAuditRepository extends JpaRepository<AssignmentAudit, UUID> {
    List<AssignmentAudit> findByOrderIdOrderByCreatedAtDesc(UUID orderId);
}
