package com.hyperlofy.backend.ai.memory;

import com.hyperlofy.backend.ai.memory.dto.MemoryDto;
import com.hyperlofy.backend.ai.memory.dto.MemoryCreateRequest;
import com.hyperlofy.backend.ai.memory.dto.MemoryUpdateRequest;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class MemoryMapper {

    public MemoryDto toDto(MemoryEntity entity) {
        if (entity == null) {
            return null;
        }
        return MemoryDto.builder()
                .memoryId(entity.getMemoryId())
                .customerId(entity.getCustomerId())
                .memoryType(entity.getMemoryType())
                .key(entity.getKey())
                .value(entity.getValue())
                .confidence(entity.getConfidence())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .lastUsedAt(entity.getLastUsedAt())
                .usageCount(entity.getUsageCount())
                .active(entity.isActive())
                .build();
    }

    public MemoryEntity toEntity(UUID customerId, MemoryCreateRequest request) {
        return MemoryEntity.builder()
                .memoryId(UUID.randomUUID())
                .customerId(customerId)
                .memoryType(request.getMemoryType())
                .key(request.getKey().trim().toLowerCase())
                .value(request.getValue().trim())
                .confidence(request.getConfidence() == null ? 0.8 : request.getConfidence())
                .lastUsedAt(OffsetDateTime.now())
                .usageCount(0)
                .active(true)
                .build();
    }

    public void updateEntity(MemoryEntity entity, MemoryUpdateRequest request) {
        entity.setMemoryType(request.getMemoryType());
        entity.setKey(request.getKey().trim().toLowerCase());
        entity.setValue(request.getValue().trim());
        if (request.getConfidence() != null) {
            entity.setConfidence(request.getConfidence());
        }
    }
}
