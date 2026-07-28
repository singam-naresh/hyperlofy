package com.hyperlofy.backend.engagement.repository;

import com.hyperlofy.backend.engagement.entity.NotificationDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationDecisionRepository extends JpaRepository<NotificationDecision, UUID> {
    Optional<NotificationDecision> findByDecisionCode(String decisionCode);
    List<NotificationDecision> findByCustomerId(UUID customerId);
}
