package com.hyperlofy.backend.seo.repository;

import com.hyperlofy.backend.seo.entity.SeoKeywordRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeoKeywordRankingRepository extends JpaRepository<SeoKeywordRanking, UUID> {
    Optional<SeoKeywordRanking> findByKeyword(String keyword);
}
