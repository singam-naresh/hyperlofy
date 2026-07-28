package com.hyperlofy.backend.data.repository;

import com.hyperlofy.backend.data.entity.LakehouseTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LakehouseTableRepository extends JpaRepository<LakehouseTable, UUID> {
    Optional<LakehouseTable> findByTableName(String tableName);
}
