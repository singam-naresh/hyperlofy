package com.hyperlofy.backend.payment.repository;

import com.hyperlofy.backend.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByOrderId(UUID orderId);
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByOrderCustomerId(UUID customerId);
}
