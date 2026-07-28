package com.hyperlofy.backend.platform.repository;

import com.hyperlofy.backend.platform.entity.ArchiveCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArchiveCatalogRepository extends JpaRepository<ArchiveCatalog, UUID> {
    List<ArchiveCatalog> findByDatasetNameOrderByCreatedAtDesc(String datasetName);
}
