package com.hyperlofy.backend.multitenancy.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "organizations")
@SQLDelete(sql = "UPDATE organizations SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organization extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "org_name", nullable = false, length = 150)
    private String orgName;

    @Column(name = "parent_org_id")
    private UUID parentOrgId;

    @Column(name = "org_code", nullable = false, unique = true, length = 100)
    private String orgCode;
}
