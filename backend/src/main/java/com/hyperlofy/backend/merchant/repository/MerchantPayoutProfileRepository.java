package com.hyperlofy.backend.merchant.repository;

import com.hyperlofy.backend.merchant.entity.MerchantPayoutProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MerchantPayoutProfileRepository extends JpaRepository<MerchantPayoutProfile, UUID> {
    Optional<MerchantPayoutProfile> findByMerchantId(UUID merchantId);
}
