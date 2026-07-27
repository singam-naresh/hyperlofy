package com.hyperlofy.backend.ai.genai.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "knowledge_documents")
@SQLDelete(sql = "UPDATE knowledge_documents SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeDocument extends BaseEntity {

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Builder.Default
    @Column(name = "category", nullable = false, length = 50)
    private String category = "FAQ";

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @Column(name = "content_type", nullable = false, length = 50)
    private String contentType = "MARKDOWN";
}
