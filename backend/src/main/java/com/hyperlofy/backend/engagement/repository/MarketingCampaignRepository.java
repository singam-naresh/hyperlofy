package com.hyperlofy.backend.engagement.repository;

import com.hyperlofy.backend.engagement.entity.MarketingCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MarketingCampaignRepository extends JpaRepository<MarketingCampaign, UUID> {
    Optional<MarketingCampaign> findByCampaignCode(String campaignCode);
}
