package com.hyperlofy.backend.finance.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "finance_entities")
@SQLDelete(sql = "UPDATE finance_entities SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanceEntity extends BaseEntity {

    @Column(name = "entity_code", nullable = false, unique = true, length = 50)
    private String entityCode;

    @Column(name = "entity_name", nullable = false, length = 100)
    private String entityName;

    @Builder.Default
    @Column(name = "country_code", nullable = false, length = 10)
    private String countryCode = "IND";

    @Column(name = "tax_registration_number", length = 50)
    private String taxRegistrationNumber;

    @Builder.Default
    @Column(name = "currency", nullable = false, length = 10)
    private String currency = "INR";
}
