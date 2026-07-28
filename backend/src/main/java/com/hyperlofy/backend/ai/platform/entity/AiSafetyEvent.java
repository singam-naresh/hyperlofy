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
@Table(name = "ai_safety_events")
@SQLDelete(sql = "UPDATE ai_safety_events SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiSafetyEvent extends BaseEntity {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "violation_type", nullable = false, length = 50)
    private String violationType; // PROMPT_INJECTION, PII_LEAK, SENSITIVE_CONTENT, HALLUCINATION

    @Builder.Default
    @Column(name = "severity", nullable = false, length = 30)
    private String severity = "MEDIUM";

    @Column(name = "sanitized_prompt")
    private String sanitizedPrompt;

    @Builder.Default
    @Column(name = "action_taken", nullable = false, length = 50)
    private String actionTaken = "BLOCKED";
}
