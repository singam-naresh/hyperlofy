package com.hyperlofy.backend.security.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "security_policies")
@SQLDelete(sql = "UPDATE security_policies SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityPolicy extends BaseEntity {

    @Column(name = "policy_code", nullable = false, unique = true, length = 100)
    private String policyCode;

    @Column(name = "policy_name", nullable = false, length = 150)
    private String policyName;

    @Column(name = "policy_type", nullable = false, length = 50)
    private String policyType; // ABAC, PBAC, ZERO_TRUST, DATA_MASKING

    @Builder.Default
    @Column(name = "effect", nullable = false, length = 20)
    private String effect = "ALLOW"; // ALLOW, DENY

    @Column(name = "rule_expression", nullable = false, columnDefinition = "TEXT")
    private String ruleExpression;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "ACTIVE";
}
