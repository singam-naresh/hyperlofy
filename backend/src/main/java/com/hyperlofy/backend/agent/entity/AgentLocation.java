package com.hyperlofy.backend.agent.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
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

import java.util.UUID;

@Entity
@Table(name = "agent_locations")
@SQLDelete(sql = "UPDATE agent_locations SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentLocation extends BaseEntity {

    @Column(name = "agent_id", nullable = false)
    private UUID agentId;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "accuracy")
    private Double accuracy;

    @Column(name = "bearing")
    private Double bearing;
}
