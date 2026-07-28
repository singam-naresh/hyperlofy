package com.hyperlofy.backend.experience.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "review_reports")
@SQLDelete(sql = "UPDATE review_reports SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReport extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private CustomerReview review;

    @Column(name = "reporter_user_id", nullable = false)
    private UUID reporterUserId;

    @Column(name = "reason", nullable = false, length = 80)
    private String reason; // SPAM, FAKE_REVIEW, OFFENSIVE_LANGUAGE, HATE_SPEECH, COPYRIGHT

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "PENDING"; // PENDING, INVESTIGATED, DISMISSED, ACTIONED
}
