package com.hyperlofy.backend.support.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "customer_csat_surveys")
@SQLDelete(sql = "UPDATE customer_csat_surveys SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCsatSurvey extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private SupportTicket ticket;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Builder.Default
    @Column(name = "csat_rating", nullable = false)
    private Integer csatRating = 5; // 1-5

    @Builder.Default
    @Column(name = "nps_score", nullable = false)
    private Integer npsScore = 10; // 0-10

    @Builder.Default
    @Column(name = "ces_score", nullable = false)
    private Integer cesScore = 5; // 1-7

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;
}
