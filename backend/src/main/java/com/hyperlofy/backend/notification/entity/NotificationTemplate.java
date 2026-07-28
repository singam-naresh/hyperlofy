package com.hyperlofy.backend.notification.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "notification_templates")
@SQLDelete(sql = "UPDATE notification_templates SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplate extends BaseEntity {

    @Column(name = "template_code", nullable = false, unique = true, length = 100)
    private String templateCode;

    @Column(name = "channel", nullable = false, length = 30)
    private String channel;

    @Column(name = "subject_template")
    private String subjectTemplate;

    @Column(name = "body_template", nullable = false)
    private String bodyTemplate;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
}
