package com.hyperlofy.backend.notification.repository;

import com.hyperlofy.backend.notification.entity.NotificationEngagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationEngagementRepository extends JpaRepository<NotificationEngagement, UUID> {
    List<NotificationEngagement> findByMessageId(UUID messageId);
}
