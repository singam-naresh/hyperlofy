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
@Table(name = "tenant_license_allocations")
@SQLDelete(sql = "UPDATE tenant_license_allocations SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantLicenseAllocation extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "license_type", nullable = false, length = 100)
    private String licenseType;

    @Builder.Default
    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats = 50;

    @Builder.Default
    @Column(name = "allocated_seats", nullable = false)
    private Integer allocatedSeats = 0;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "ACTIVE";
}
