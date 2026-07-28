package com.hyperlofy.backend.support.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "knowledge_articles")
@SQLDelete(sql = "UPDATE knowledge_articles SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeArticle extends BaseEntity {

    @Column(name = "article_code", nullable = false, unique = true, length = 100)
    private String articleCode;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @Column(name = "category", nullable = false, length = 80)
    private String category = "CUSTOMER_FAQ"; // CUSTOMER_FAQ, AGENT_SOP, MERCHANT_SOP

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Builder.Default
    @Column(name = "useful_count", nullable = false)
    private Integer usefulCount = 0;

    @Column(name = "tenant_id")
    private UUID tenantId;
}
