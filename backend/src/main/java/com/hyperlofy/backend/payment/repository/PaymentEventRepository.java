package com.hyperlofy.backend.payment.repository;

import com.hyperlofy.backend.payment.entity.PaymentEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentEventRepository extends JpaRepository<PaymentEvent, UUID> {
}
