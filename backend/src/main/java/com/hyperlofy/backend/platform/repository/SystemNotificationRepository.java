package com.hyperlofy.backend.platform.repository;

import com.hyperlofy.backend.platform.entity.SystemNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SystemNotificationRepository extends JpaRepository<SystemNotification, UUID> {
    List<SystemNotification> findByTargetGroup(String targetGroup);
}
