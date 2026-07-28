package com.hyperlofy.backend.seo.repository;

import com.hyperlofy.backend.seo.entity.SeoSitemap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeoSitemapRepository extends JpaRepository<SeoSitemap, UUID> {
    Optional<SeoSitemap> findBySitemapCode(String sitemapCode);
}
