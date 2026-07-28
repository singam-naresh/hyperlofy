package com.hyperlofy.backend.search.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
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

    @Column(name = "article_key", nullable = false, unique = true, length = 100)
    private String articleKey;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "article_type", nullable = false, length = 80)
    private String articleType; // SOP, GUIDE, POLICY, FAQ, RUNBOOK, PLAYBOOK, DOCUMENT, ENGINEERING

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "DRAFT"; // DRAFT, REVIEW, PUBLISHED, ARCHIVED, DEPRECATED

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "content_summary", columnDefinition = "TEXT")
    private String contentSummary;

    @Column(name = "category_id")
    private UUID categoryId;

    @Column(name = "author_user_id", nullable = false)
    private UUID authorUserId;

    @Column(name = "reviewer_user_id")
    private UUID reviewerUserId;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;

    @Builder.Default
    @Column(name = "version", nullable = false)
    private Integer version = 1;

    @Column(name = "tags", length = 500)
    private String tags;

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Builder.Default
    @Column(name = "helpful_votes", nullable = false)
    private Integer helpfulVotes = 0;

    @Column(name = "tenant_id")
    private UUID tenantId;
}
