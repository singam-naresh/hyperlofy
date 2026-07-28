package com.hyperlofy.backend.ai.platform.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "ai_prompts")
@SQLDelete(sql = "UPDATE ai_prompts SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiPrompt extends BaseEntity {

    @Column(name = "prompt_key", nullable = false, unique = true, length = 100)
    private String promptKey;

    @Column(name = "prompt_name", nullable = false, length = 150)
    private String promptName;

    @Column(name = "template_text", nullable = false)
    private String templateText;

    @Builder.Default
    @Column(name = "version", nullable = false, length = 30)
    private String version = "v1.0.0";

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
