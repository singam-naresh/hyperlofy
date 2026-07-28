package com.hyperlofy.backend.devex.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "api_contracts")
@SQLDelete(sql = "UPDATE api_contracts SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiContract extends BaseEntity {

    @Column(name = "contract_name", nullable = false, unique = true, length = 150)
    private String contractName;

    @Column(name = "api_name", nullable = false, length = 100)
    private String apiName;

    @Column(name = "consumer_service", nullable = false, length = 100)
    private String consumerService;

    @Builder.Default
    @Column(name = "schema_version", nullable = false, length = 30)
    private String schemaVersion = "v1.0.0";

    @Builder.Default
    @Column(name = "validation_status", nullable = false, length = 30)
    private String validationStatus = "VALIDATED";
}
