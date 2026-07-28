package com.hyperlofy.backend.analytics.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "analytics_dashboards")
@SQLDelete(sql = "UPDATE analytics_dashboards SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDashboard extends BaseEntity {

    @Column(name = "dashboard_key", nullable = false, unique = true, length = 100)
    private String dashboardKey;

    @Column(name = "dashboard_title", nullable = false, length = 150)
    private String dashboardTitle;

    @Column(name = "config_json")
    private String configJson;
}
