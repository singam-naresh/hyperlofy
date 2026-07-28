package com.hyperlofy.backend.experience.repository;

import com.hyperlofy.backend.experience.entity.MerchantReputation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MerchantReputationRepository extends JpaRepository<MerchantReputation, UUID> {
    Optional<MerchantReputation> findByMerchantId(UUID merchantId);
}
