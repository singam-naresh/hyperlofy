package com.hyperlofy.backend.ai.memory.dto;

import com.hyperlofy.backend.ai.memory.MemoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoryDto {
    private UUID memoryId;
    private UUID customerId;
    private MemoryType memoryType;
    private String key;
    private String value;
    private double confidence;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime lastUsedAt;
    private long usageCount;
    private boolean active;
}
