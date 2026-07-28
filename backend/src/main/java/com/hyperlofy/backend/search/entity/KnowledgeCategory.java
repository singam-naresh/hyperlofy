package com.hyperlofy.backend.search.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "knowledge_categories")
@SQLDelete(sql = "UPDATE knowledge_categories SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeCategory extends BaseEntity {

    @Column(name = "category_key", nullable = false, unique = true, length = 100)
    private String categoryKey;

    @Column(name = "category_name", nullable = false, length = 150)
    private String categoryName;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(name = "icon", length = 50)
    private String icon;

    @Builder.Default
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Builder.Default
    @Column(name = "article_count", nullable = false)
    private Integer articleCount = 0;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
