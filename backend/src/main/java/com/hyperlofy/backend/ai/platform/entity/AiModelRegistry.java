package com.hyperlofy.backend.ai.platform.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "ai_model_registry")
@SQLDelete(sql = "UPDATE ai_model_registry SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiModelRegistry extends BaseEntity {

    @Column(name = "model_name", nullable = false, unique = true, length = 100)
    private String modelName;

    @Column(name = "provider", nullable = false, length = 50)
    private String provider; // GEMINI, OPENAI, ANTHROPIC, LOCAL

    @Column(name = "endpoint_url", length = 255)
    private String endpointUrl;

    @Builder.Default
    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = true;

    @Builder.Default
    @Column(name = "latency_ms")
    private Integer latencyMs = 120;
}
