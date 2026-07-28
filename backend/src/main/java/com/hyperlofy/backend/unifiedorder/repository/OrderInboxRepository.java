package com.hyperlofy.backend.unifiedorder.repository;

import com.hyperlofy.backend.unifiedorder.entity.OrderInbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderInboxRepository extends JpaRepository<OrderInbox, UUID> {
    Optional<OrderInbox> findByMessageId(String messageId);
}
