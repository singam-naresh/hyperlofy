package com.hyperlofy.backend.payments.repository;

import com.hyperlofy.backend.payments.entity.PaymentSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentSubscriptionRepository extends JpaRepository<PaymentSubscription, UUID> {
    List<PaymentSubscription> findByCustomerId(UUID customerId);
}
