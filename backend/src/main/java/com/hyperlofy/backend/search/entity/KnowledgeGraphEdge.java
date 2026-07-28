package com.hyperlofy.backend.search.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "knowledge_graph_edges")
@SQLDelete(sql = "UPDATE knowledge_graph_edges SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeGraphEdge extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_node_id", nullable = false)
    private KnowledgeGraphNode sourceNode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_node_id", nullable = false)
    private KnowledgeGraphNode targetNode;

    @Column(name = "relationship_type", nullable = false, length = 80)
    private String relationshipType; // PURCHASED, VIEWED, BELONGS_TO, ASSIGNED_TO, CREATED_BY, APPROVED_BY, RELATED_TO

    @Builder.Default
    @Column(name = "weight", nullable = false, precision = 5, scale = 4)
    private BigDecimal weight = new BigDecimal("1.0000");

    @Column(name = "tenant_id")
    private UUID tenantId;
}
