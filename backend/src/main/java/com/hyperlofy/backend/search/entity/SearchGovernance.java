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
@Table(name = "search_governance")
@SQLDelete(sql = "UPDATE search_governance SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchGovernance extends BaseEntity {

    @Column(name = "document_id", nullable = false, unique = true, length = 150)
    private String documentId;

    @Builder.Default
    @Column(name = "sensitivity_level", nullable = false, length = 30)
    private String sensitivityLevel = "INTERNAL"; // PUBLIC, INTERNAL, RESTRICTED, CONFIDENTIAL

    @Builder.Default
    @Column(name = "classification", nullable = false, length = 80)
    private String classification = "STANDARD";

    @Column(name = "owner_user_id", nullable = false)
    private UUID ownerUserId;

    @Builder.Default
    @Column(name = "access_roles", nullable = false, length = 255)
    private String accessRoles = "ROLE_USER";

    @Column(name = "tenant_id")
    private UUID tenantId;
}
