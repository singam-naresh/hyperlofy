package com.hyperlofy.backend.referral.repository;

import com.hyperlofy.backend.referral.entity.Referral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, UUID> {
    List<Referral> findByReferrerId(UUID referrerId);
    List<Referral> findByReferredId(UUID referredId);
}
