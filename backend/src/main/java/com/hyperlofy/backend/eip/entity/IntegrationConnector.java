package com.hyperlofy.backend.eip.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "integration_connectors")
@SQLDelete(sql = "UPDATE integration_connectors SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationConnector extends BaseEntity {

    @Column(name = "connector_code", nullable = false, unique = true, length = 100)
    private String connectorCode;

    @Column(name = "connector_name", nullable = false, length = 150)
    private String connectorName;

    @Column(name = "system_type", nullable = false, length = 50)
    private String systemType; // ERP, CRM, ACCOUNTING, SHIPPING

    @Column(name = "provider_name", nullable = false, length = 100)
    private String providerName; // SAP, SALESFORCE, TALLY, QUICKBOOKS, FEDEX

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, DEGRADED

    @Column(name = "endpoint_url", nullable = false, length = 255)
    private String endpointUrl;
}
