package com.hyperlofy.backend.settlement.repository;

import com.hyperlofy.backend.settlement.entity.BeneficiaryAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BeneficiaryAccountRepository extends JpaRepository<BeneficiaryAccount, UUID> {
    Optional<BeneficiaryAccount> findByOwnerId(UUID ownerId);
}
