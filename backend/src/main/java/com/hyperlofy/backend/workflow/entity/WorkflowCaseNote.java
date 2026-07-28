package com.hyperlofy.backend.workflow.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "workflow_case_notes")
@SQLDelete(sql = "UPDATE workflow_case_notes SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowCaseNote extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private WorkflowCase workflowCase;

    @Column(name = "author_user_id", nullable = false)
    private UUID authorUserId;

    /**
     * Note types: NOTE, EVIDENCE, ATTACHMENT, DECISION
     */
    @Builder.Default
    @Column(name = "note_type", nullable = false, length = 30)
    private String noteType = "NOTE";

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "attachment_url", length = 500)
    private String attachmentUrl;

    @Builder.Default
    @Column(name = "is_internal", nullable = false)
    private Boolean isInternal = false;
}
