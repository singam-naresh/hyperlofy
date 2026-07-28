package com.hyperlofy.backend.support.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "support_ticket_messages")
@SQLDelete(sql = "UPDATE support_ticket_messages SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketMessage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private SupportTicket ticket;

    @Column(name = "sender_user_id", nullable = false)
    private UUID senderUserId;

    @Builder.Default
    @Column(name = "sender_role", nullable = false, length = 50)
    private String senderRole = "CUSTOMER"; // CUSTOMER, AGENT, SUPERVISOR, AI_BOT

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @Column(name = "is_internal_note", nullable = false)
    private Boolean isInternalNote = false;

    @Column(name = "attachment_urls", length = 1000)
    private String attachmentUrls;
}
