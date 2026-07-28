package com.hyperlofy.backend.search.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "search_suggestions")
@SQLDelete(sql = "UPDATE search_suggestions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchSuggestion extends BaseEntity {

    @Column(name = "suggestion_text", nullable = false, length = 300)
    private String suggestionText;

    @Builder.Default
    @Column(name = "suggestion_type", nullable = false, length = 30)
    private String suggestionType = "AUTOCOMPLETE"; // AUTOCOMPLETE, TRENDING, SYNONYM, RELATED, POPULAR

    @Column(name = "domain", length = 80)
    private String domain;

    @Builder.Default
    @Column(name = "frequency", nullable = false)
    private Integer frequency = 1;

    @Column(name = "synonym_for", length = 300)
    private String synonymFor;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "tenant_id")
    private UUID tenantId;
}
