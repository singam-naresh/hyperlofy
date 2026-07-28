package com.hyperlofy.backend.notification.repository;

import com.hyperlofy.backend.notification.entity.NotificationProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationProviderRepository extends JpaRepository<NotificationProvider, UUID> {
    List<NotificationProvider> findByChannelAndIsActiveTrueOrderByPriorityAsc(String channel);
}
