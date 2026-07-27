package com.hyperlofy.backend.ai.recommendation.repository;

import com.hyperlofy.backend.ai.recommendation.entity.CustomerBehaviourEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerBehaviourEventRepository extends JpaRepository<CustomerBehaviourEvent, UUID> {
    List<CustomerBehaviourEvent> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<CustomerBehaviourEvent> findByEventType(String eventType);
}
