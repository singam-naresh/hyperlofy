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
@Table(name = "knowledge_graph_nodes")
@SQLDelete(sql = "UPDATE knowledge_graph_nodes SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeGraphNode extends BaseEntity {

    @Column(name = "entity_id", nullable = false, unique = true, length = 150)
    private String entityId;

    @Column(name = "entity_type", nullable = false, length = 80)
    private String entityType; // MERCHANT, PRODUCT, CUSTOMER, ORDER, WORKFLOW, CASE, DOCUMENT

    @Column(name = "entity_name", nullable = false, length = 255)
    private String entityName;

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    @Column(name = "tenant_id")
    private UUID tenantId;
}
