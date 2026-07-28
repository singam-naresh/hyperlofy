package com.hyperlofy.backend.ai.platform.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "ai_memory_store")
@SQLDelete(sql = "UPDATE ai_memory_store SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiMemoryStore extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "memory_key", nullable = false, length = 100)
    private String memoryKey;

    @Column(name = "memory_value", nullable = false)
    private String memoryValue;

    @Builder.Default
    @Column(name = "memory_type", nullable = false, length = 50)
    private String memoryType = "CONVERSATION"; // CONVERSATION, PREFERENCE, VECTOR
}
