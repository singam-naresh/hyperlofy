package com.hyperlofy.backend.experience.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "review_replies")
@SQLDelete(sql = "UPDATE review_replies SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReply extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private CustomerReview review;

    @Column(name = "replier_user_id", nullable = false)
    private UUID replierUserId;

    @Builder.Default
    @Column(name = "replier_role", nullable = false, length = 50)
    private String replierRole = "MERCHANT"; // MERCHANT, SUPPORT, ADMIN

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false;
}
