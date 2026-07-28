package com.hyperlofy.backend.notification.repository;

import com.hyperlofy.backend.notification.entity.NotificationConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationConsentRepository extends JpaRepository<NotificationConsent, UUID> {
    Optional<NotificationConsent> findByUserId(UUID userId);
}
