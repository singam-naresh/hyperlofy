package com.hyperlofy.backend.ai.genai.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "knowledge_document_chunks")
@SQLDelete(sql = "UPDATE knowledge_document_chunks SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeDocumentChunk extends BaseEntity {

    @Column(name = "document_id", nullable = false)
    private UUID documentId;

    @Builder.Default
    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex = 0;

    @Column(name = "chunk_content", nullable = false, columnDefinition = "TEXT")
    private String chunkContent;

    @Column(name = "embedding_vector", columnDefinition = "TEXT")
    private String embeddingVector;
}
