package com.hyperlofy.backend.order.repository;

import com.hyperlofy.backend.order.entity.Order;
import com.hyperlofy.backend.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
    List<Order> findByAgentIdOrderByCreatedAtDesc(UUID agentId);
    List<Order> findByOrderStatusOrderByCreatedAtDesc(OrderStatus status);
    List<Order> findByZoneIdOrderByCreatedAtDesc(UUID zoneId);
    List<Order> findByMerchantIdOrderByCreatedAtDesc(UUID merchantId);
}
