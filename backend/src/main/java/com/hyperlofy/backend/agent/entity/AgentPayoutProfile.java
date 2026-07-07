package com.hyperlofy.backend.agent.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "agent_payout_profiles")
@SQLDelete(sql = "UPDATE agent_payout_profiles SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentPayoutProfile extends BaseEntity {

    @Column(name = "agent_id", nullable = false, unique = true)
    private UUID agentId;

    @Column(name = "bank_account_number", nullable = false, length = 50)
    private String bankAccountNumber;

    @Column(name = "bank_ifsc_code", nullable = false, length = 20)
    private String bankIfscCode;

    @Column(name = "bank_holder_name", nullable = false, length = 150)
    private String bankHolderName;

    @Column(name = "cumulative_earnings", nullable = false, precision = 12, scale = 2)
    private BigDecimal cumulativeEarnings = BigDecimal.ZERO;

    @Column(name = "current_balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal currentBalance = BigDecimal.ZERO;
}
