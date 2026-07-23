package com.hyperlofy.backend.ai.memory;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "memories", indexes = {
        @Index(name = "idx_memories_customer_active", columnList = "customer_id,active"),
        @Index(name = "idx_memories_customer_type", columnList = "customer_id,memory_type"),
        @Index(name = "idx_memories_last_used_at", columnList = "last_used_at")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoryEntity extends BaseEntity {

    @Column(name = "memory_id", nullable = false, unique = true, updatable = false)
    private UUID memoryId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "memory_type", nullable = false, length = 50)
    private MemoryType memoryType;

    @Column(name = "key", nullable = false, length = 120)
    private String key;

    @Column(name = "value", nullable = false, length = 500)
    private String value;

    @Column(name = "confidence", nullable = false)
    private double confidence;

    @Column(name = "last_used_at")
    private OffsetDateTime lastUsedAt;

    @Column(name = "usage_count", nullable = false)
    private long usageCount;

    @Column(name = "active", nullable = false)
    private boolean active;
}
