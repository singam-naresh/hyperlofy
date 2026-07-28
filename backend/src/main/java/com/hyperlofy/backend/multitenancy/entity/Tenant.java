package com.hyperlofy.backend.multitenancy.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "tenants")
@SQLDelete(sql = "UPDATE tenants SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant extends BaseEntity {

    @Column(name = "tenant_code", nullable = false, unique = true, length = 100)
    private String tenantCode;

    @Column(name = "tenant_name", nullable = false, length = 150)
    private String tenantName;

    @Column(name = "domain_name", nullable = false, unique = true, length = 255)
    private String domainName;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "ACTIVE"; // ACTIVE, SUSPENDED, PROVISIONING

    @Builder.Default
    @Column(name = "country_code", nullable = false, length = 10)
    private String countryCode = "IN";

    @Builder.Default
    @Column(name = "currency_code", nullable = false, length = 10)
    private String currencyCode = "INR";
}
