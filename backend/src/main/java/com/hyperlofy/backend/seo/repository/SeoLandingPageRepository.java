package com.hyperlofy.backend.seo.repository;

import com.hyperlofy.backend.seo.entity.SeoLandingPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeoLandingPageRepository extends JpaRepository<SeoLandingPage, UUID> {
    Optional<SeoLandingPage> findByLandingCode(String landingCode);
    Optional<SeoLandingPage> findByPagePath(String pagePath);
}
