package com.hyperlofy.backend.data.repository;

import com.hyperlofy.backend.data.entity.DatasetRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DatasetRegistryRepository extends JpaRepository<DatasetRegistry, UUID> {
    Optional<DatasetRegistry> findByDatasetName(String datasetName);
}
