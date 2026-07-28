package com.hyperlofy.backend.data.repository;

import com.hyperlofy.backend.data.entity.FeatureStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeatureStoreRepository extends JpaRepository<FeatureStore, UUID> {
    Optional<FeatureStore> findByEntityTypeAndEntityIdAndFeatureName(String entityType, String entityId, String featureName);
    List<FeatureStore> findByEntityTypeAndEntityId(String entityType, String entityId);
}
