package com.hyperlofy.backend.analytics.repository;

import com.hyperlofy.backend.analytics.entity.AnalyticsSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AnalyticsSnapshotRepository extends JpaRepository<AnalyticsSnapshot, java.util.UUID> {
    Optional<AnalyticsSnapshot> findBySnapshotDate(LocalDate date);
}
