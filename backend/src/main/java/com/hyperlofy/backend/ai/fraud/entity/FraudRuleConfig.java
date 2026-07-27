package com.hyperlofy.backend.ai.fraud.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "fraud_rule_configs")
@SQLDelete(sql = "UPDATE fraud_rule_configs SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudRuleConfig extends BaseEntity {

    @Column(name = "rule_name", nullable = false, unique = true, length = 100)
    private String ruleName;

    @Column(name = "description", length = 255)
    private String description;

    @Builder.Default
    @Column(name = "weight")
    private Double weight = 1.0;

    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean isEnabled = true;
}
