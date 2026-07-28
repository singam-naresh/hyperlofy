package com.hyperlofy.backend.notification.repository;

import com.hyperlofy.backend.notification.entity.NotificationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationMessageRepository extends JpaRepository<NotificationMessage, UUID> {
    List<NotificationMessage> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId);
}
