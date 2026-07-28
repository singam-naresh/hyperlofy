package com.hyperlofy.backend.devex.repository;

import com.hyperlofy.backend.devex.entity.PartnerApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PartnerApplicationRepository extends JpaRepository<PartnerApplication, UUID> {
    Optional<PartnerApplication> findByClientId(String clientId);
}
