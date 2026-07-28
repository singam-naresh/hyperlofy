package com.hyperlofy.backend.unifiedorder.repository;

import com.hyperlofy.backend.unifiedorder.entity.OrderIdempotency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderIdempotencyRepository extends JpaRepository<OrderIdempotency, UUID> {
    Optional<OrderIdempotency> findByIdempotencyKey(String idempotencyKey);
}
