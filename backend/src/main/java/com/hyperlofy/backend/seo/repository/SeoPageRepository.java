package com.hyperlofy.backend.seo.repository;

import com.hyperlofy.backend.seo.entity.SeoPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeoPageRepository extends JpaRepository<SeoPage, UUID> {
    Optional<SeoPage> findByPageUrl(String pageUrl);
}
