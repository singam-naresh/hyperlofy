package com.hyperlofy.backend.payment.repository;

import com.hyperlofy.backend.payment.entity.Refund;
import com.hyperlofy.backend.payment.entity.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RefundRepository extends JpaRepository<Refund, UUID> {
    List<Refund> findByPaymentId(UUID paymentId);
    List<Refund> findByRefundStatus(RefundStatus status);
}
