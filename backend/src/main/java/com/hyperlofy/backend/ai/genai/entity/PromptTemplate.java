package com.hyperlofy.backend.ai.genai.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "prompt_templates")
@SQLDelete(sql = "UPDATE prompt_templates SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptTemplate extends BaseEntity {

    @Column(name = "template_key", nullable = false, unique = true, length = 100)
    private String templateKey;

    @Builder.Default
    @Column(name = "version", nullable = false)
    private Integer version = 1;

    @Builder.Default
    @Column(name = "category", nullable = false, length = 50)
    private String category = "GENERAL";

    @Column(name = "system_prompt", nullable = false, columnDefinition = "TEXT")
    private String systemPrompt;

    @Column(name = "user_prompt_template", nullable = false, columnDefinition = "TEXT")
    private String userPromptTemplate;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
}
