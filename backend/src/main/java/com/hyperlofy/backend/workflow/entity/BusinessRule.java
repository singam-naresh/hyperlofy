package com.hyperlofy.backend.workflow.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "business_rules")
@SQLDelete(sql = "UPDATE business_rules SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessRule extends BaseEntity {

    @Column(name = "rule_key", nullable = false, unique = true, length = 100)
    private String ruleKey;

    @Column(name = "rule_name", nullable = false, length = 150)
    private String ruleName;

    /**
     * Rule categories: REFUND_APPROVAL, MERCHANT_RATING, DELIVERY_ASSIGNMENT,
     * FRAUD_SCORING, ESCALATION_TRIGGER
     */
    @Column(name = "rule_category", nullable = false, length = 80)
    private String ruleCategory;

    @Column(name = "condition_field", nullable = false, length = 100)
    private String conditionField;

    /**
     * Operators: LT, GT, LTE, GTE, EQ, BETWEEN
     */
    @Column(name = "condition_operator", nullable = false, length = 30)
    private String conditionOperator;

    @Column(name = "condition_value_min", precision = 16, scale = 2)
    private BigDecimal conditionValueMin;

    @Column(name = "condition_value_max", precision = 16, scale = 2)
    private BigDecimal conditionValueMax;

    /**
     * Actions: AUTO_APPROVE, REQUIRE_REVIEW, ESCALATE, REJECT, ASSIGN_POOL
     */
    @Column(name = "action_type", nullable = false, length = 80)
    private String actionType;

    @Column(name = "action_value", length = 255)
    private String actionValue;

    @Builder.Default
    @Column(name = "priority", nullable = false)
    private Integer priority = 10;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "effective_from")
    private OffsetDateTime effectiveFrom = OffsetDateTime.now();

    @Column(name = "effective_to")
    private OffsetDateTime effectiveTo;
}
