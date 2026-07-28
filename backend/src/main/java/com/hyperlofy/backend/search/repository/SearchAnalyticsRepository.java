package com.hyperlofy.backend.search.repository;

import com.hyperlofy.backend.search.entity.SearchAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SearchAnalyticsRepository extends JpaRepository<SearchAnalytics, UUID> {
    List<SearchAnalytics> findByIsZeroResultTrue();
    List<SearchAnalytics> findByDomain(String domain);
}
