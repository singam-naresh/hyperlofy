package com.hyperlofy.backend.notification.repository;

import com.hyperlofy.backend.notification.entity.NotificationCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationCampaignRepository extends JpaRepository<NotificationCampaign, UUID> {
    Optional<NotificationCampaign> findByCampaignName(String campaignName);
}
