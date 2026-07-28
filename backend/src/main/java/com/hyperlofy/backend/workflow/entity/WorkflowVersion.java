package com.hyperlofy.backend.workflow.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "workflow_versions")
@SQLDelete(sql = "UPDATE workflow_versions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowVersion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "definition_id", nullable = false)
    private WorkflowDefinition definition;

    @Builder.Default
    @Column(name = "version_number", nullable = false)
    private Integer versionNumber = 1;

    /**
     * BPM version lifecycle: DRAFT → ACTIVE → ARCHIVED
     */
    @Builder.Default
    @Column(name = "version_status", nullable = false, length = 30)
    private String versionStatus = "DRAFT";

    @Column(name = "version_notes", columnDefinition = "TEXT")
    private String versionNotes;

    @Column(name = "published_by")
    private UUID publishedBy;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;

    @Column(name = "archived_at")
    private OffsetDateTime archivedAt;

    /** Raw BPMN 2.0 XML descriptor for this version */
    @Column(name = "bpmn_xml", columnDefinition = "TEXT")
    private String bpmnXml;
}
