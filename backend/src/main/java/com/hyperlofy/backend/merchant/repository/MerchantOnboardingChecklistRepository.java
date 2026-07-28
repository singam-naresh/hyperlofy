package com.hyperlofy.backend.merchant.repository;

import com.hyperlofy.backend.merchant.entity.MerchantOnboardingChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MerchantOnboardingChecklistRepository extends JpaRepository<MerchantOnboardingChecklist, UUID> {
    Optional<MerchantOnboardingChecklist> findByMerchantId(UUID merchantId);
}
