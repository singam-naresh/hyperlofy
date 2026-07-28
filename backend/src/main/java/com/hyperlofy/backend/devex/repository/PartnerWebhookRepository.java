package com.hyperlofy.backend.devex.repository;

import com.hyperlofy.backend.devex.entity.PartnerWebhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PartnerWebhookRepository extends JpaRepository<PartnerWebhook, UUID> {
    List<PartnerWebhook> findByPartnerAppId(UUID partnerAppId);
}
