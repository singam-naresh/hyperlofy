package com.hyperlofy.backend.platform.repository;

import com.hyperlofy.backend.platform.entity.CapacityForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CapacityForecastRepository extends JpaRepository<CapacityForecast, UUID> {
    List<CapacityForecast> findByResourceType(String resourceType);
}
