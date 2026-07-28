package com.hyperlofy.backend.security.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "user_preferences")
@SQLDelete(sql = "UPDATE user_preferences SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Builder.Default
    @Column(name = "preferred_language", length = 10)
    private String preferredLanguage = "en";

    @Builder.Default
    @Column(name = "timezone", length = 50)
    private String timezone = "UTC";

    @Builder.Default
    @Column(name = "theme", length = 20)
    private String theme = "LIGHT";

    @Builder.Default
    @Column(name = "email_notifications")
    private Boolean emailNotifications = true;

    @Builder.Default
    @Column(name = "sms_notifications")
    private Boolean smsNotifications = true;

    @Builder.Default
    @Column(name = "push_notifications")
    private Boolean pushNotifications = true;
}
