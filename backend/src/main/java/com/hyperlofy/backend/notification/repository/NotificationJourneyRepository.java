package com.hyperlofy.backend.notification.repository;

import com.hyperlofy.backend.notification.entity.NotificationJourney;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationJourneyRepository extends JpaRepository<NotificationJourney, UUID> {
    List<NotificationJourney> findByUserId(UUID userId);
}
