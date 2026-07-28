package com.hyperlofy.backend.global.repository;

import com.hyperlofy.backend.global.entity.GlobalCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GlobalCertificateRepository extends JpaRepository<GlobalCertificate, UUID> {
    Optional<GlobalCertificate> findByDomainName(String domainName);
}
