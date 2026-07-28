package com.hyperlofy.backend.unifiedorder.repository;

import com.hyperlofy.backend.unifiedorder.entity.OrderOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderOutboxRepository extends JpaRepository<OrderOutbox, UUID> {
    List<OrderOutbox> findByIsPublishedFalseOrderByCreatedAtAsc();
}
