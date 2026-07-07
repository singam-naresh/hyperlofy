package com.hyperlofy.backend.order.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import com.hyperlofy.backend.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "assignment_histories")
@SQLDelete(sql = "UPDATE assignment_histories SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentHistory extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private User agent;

    @Column(name = "assignment_time", nullable = false)
    private OffsetDateTime assignmentTime;

    @Column(name = "acceptance_time")
    private OffsetDateTime acceptanceTime;

    @Column(name = "rejection_time")
    private OffsetDateTime rejectionTime;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "reassignment_reason")
    private String reassignmentReason;
}
