package com.hyperlofy.backend.payment.repository;

import com.hyperlofy.backend.payment.entity.RefundAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RefundAuditRepository extends JpaRepository<RefundAudit, UUID> {
    List<RefundAudit> findByRefundId(UUID refundId);
}
