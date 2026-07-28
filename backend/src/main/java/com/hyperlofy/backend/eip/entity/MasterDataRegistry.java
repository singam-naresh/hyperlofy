package com.hyperlofy.backend.eip.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "master_data_registry")
@SQLDelete(sql = "UPDATE master_data_registry SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterDataRegistry extends BaseEntity {

    @Column(name = "domain_type", nullable = false, length = 50)
    private String domainType; // CUSTOMER, MERCHANT, PRODUCT, INVENTORY

    @Column(name = "master_code", nullable = false, unique = true, length = 100)
    private String masterCode;

    @Column(name = "golden_record_json", nullable = false, columnDefinition = "TEXT")
    private String goldenRecordJson;

    @Builder.Default
    @Column(name = "version", nullable = false)
    private Integer version = 1;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "ACTIVE";
}
