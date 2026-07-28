package com.hyperlofy.backend.notification.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;

@Entity
@Table(name = "notification_campaigns")
@SQLDelete(sql = "UPDATE notification_campaigns SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCampaign extends BaseEntity {

    @Column(name = "campaign_name", nullable = false, unique = true, length = 100)
    private String campaignName;

    @Column(name = "channel", nullable = false, length = 30)
    private String channel;

    @Column(name = "target_segment", nullable = false, length = 100)
    private String targetSegment;

    @Column(name = "template_code", nullable = false, length = 100)
    private String templateCode;

    @Column(name = "scheduled_at")
    private ZonedDateTime scheduledAt;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "DRAFT"; // DRAFT, APPROVED, LAUNCHED, COMPLETED

    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;
}
