package com.hyperlofy.backend.data.repository;

import com.hyperlofy.backend.data.entity.DataPipeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DataPipelineRepository extends JpaRepository<DataPipeline, UUID> {
    Optional<DataPipeline> findByPipelineCode(String pipelineCode);
}
