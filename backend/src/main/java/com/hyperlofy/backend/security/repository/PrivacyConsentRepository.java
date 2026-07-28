package com.hyperlofy.backend.security.repository;

import com.hyperlofy.backend.security.entity.PrivacyConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrivacyConsentRepository extends JpaRepository<PrivacyConsent, UUID> {
    List<PrivacyConsent> findByUserId(UUID userId);
}
