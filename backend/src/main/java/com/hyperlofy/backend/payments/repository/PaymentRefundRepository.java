package com.hyperlofy.backend.payments.repository;

import com.hyperlofy.backend.payments.entity.PaymentRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRefundRepository extends JpaRepository<PaymentRefund, UUID> {
    List<PaymentRefund> findByPaymentId(UUID paymentId);
}
