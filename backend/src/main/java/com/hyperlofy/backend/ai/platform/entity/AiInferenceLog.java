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
@Table(name = "ai_inference_logs")
@SQLDelete(sql = "UPDATE ai_inference_logs SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiInferenceLog extends BaseEntity {

    @Column(name = "model_name", nullable = false, length = 100)
    private String modelName;

    @Column(name = "prompt_key", length = 100)
    private String promptKey;

    @Column(name = "user_id")
    private UUID userId;

    @Builder.Default
    @Column(name = "token_count")
    private Integer tokenCount = 256;

    @Builder.Default
    @Column(name = "execution_time_ms")
    private Integer executionTimeMs = 145;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "SUCCESS";
}
