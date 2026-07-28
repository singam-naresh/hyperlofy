package com.hyperlofy.backend.sre.repository;

import com.hyperlofy.backend.sre.entity.PlatformCapacityForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlatformCapacityForecastRepository extends JpaRepository<PlatformCapacityForecast, UUID> {
    List<PlatformCapacityForecast> findByClusterNameOrderByCreatedAtDesc(String clusterName);
}
