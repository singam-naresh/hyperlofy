package com.hyperlofy.backend.data.repository;

import com.hyperlofy.backend.data.entity.DataQualityResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DataQualityResultRepository extends JpaRepository<DataQualityResult, UUID> {
    List<DataQualityResult> findByDatasetId(UUID datasetId);
}
