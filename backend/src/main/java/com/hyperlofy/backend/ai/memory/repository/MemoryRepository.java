package com.hyperlofy.backend.ai.memory.repository;

import com.hyperlofy.backend.ai.memory.MemoryEntity;
import com.hyperlofy.backend.ai.memory.MemoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemoryRepository extends JpaRepository<MemoryEntity, UUID> {

    List<MemoryEntity> findByCustomerIdAndActiveTrueOrderByLastUsedAtDesc(UUID customerId);

    Optional<MemoryEntity> findByCustomerIdAndMemoryTypeAndKeyAndActiveTrue(UUID customerId, MemoryType memoryType, String key);

    List<MemoryEntity> findByCustomerIdAndMemoryTypeAndActiveTrue(UUID customerId, MemoryType memoryType);

}
