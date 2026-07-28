package com.hyperlofy.backend.payments.repository;

import com.hyperlofy.backend.payments.entity.PaymentGatewayRouting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentGatewayRoutingRepository extends JpaRepository<PaymentGatewayRouting, UUID> {
    List<PaymentGatewayRouting> findByIsActiveTrueAndIsBlacklistedFalseOrderByPriorityOrderAsc();
}
