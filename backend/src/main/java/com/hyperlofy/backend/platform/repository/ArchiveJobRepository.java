package com.hyperlofy.backend.platform.repository;

import com.hyperlofy.backend.platform.entity.ArchiveJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArchiveJobRepository extends JpaRepository<ArchiveJob, UUID> {
    List<ArchiveJob> findByDatasetNameOrderByCreatedAtDesc(String datasetName);
}
