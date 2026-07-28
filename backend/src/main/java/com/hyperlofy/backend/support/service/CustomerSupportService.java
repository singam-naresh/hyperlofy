package com.hyperlofy.backend.support.service;

import com.hyperlofy.backend.support.entity.*;
import com.hyperlofy.backend.support.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerSupportService {

    private static final Logger log = LoggerFactory.getLogger(CustomerSupportService.class);

    private final SupportTicketRepository ticketRepository;
    private final SupportTicketMessageRepository messageRepository;
    private final RefundCaseRepository refundRepository;
    private final ReturnCaseRepository returnRepository;
    private final ReplacementCaseRepository replacementRepository;
    private final KnowledgeArticleRepository knowledgeRepository;
    private final CustomerCsatSurveyRepository csatRepository;

    @Transactional
    public SupportTicket createTicket(String ticketCode, UUID customerId, UUID orderId, String category, String priority, UUID tenantId) {
        log.info("[CUSTOMER SUPPORT] Creating Support Ticket Code={}, Customer={}, Category={}", ticketCode, customerId, category);

        SupportTicket ticket = ticketRepository.findByTicketCode(ticketCode).orElseGet(() ->
                SupportTicket.builder()
                        .ticketCode(ticketCode)
                        .customerId(customerId)
                        .orderId(orderId)
                        .category(category != null ? category : "REFUND")
                        .priority(priority != null ? priority : "MEDIUM")
                        .status("OPEN")
                        .slaDueTime(OffsetDateTime.now().plusHours(4))
                        .isSlaBreached(false)
                        .tenantId(tenantId)
                        .build()
        );

        return ticketRepository.save(ticket);
    }

    @Transactional
    public SupportTicket assignTicket(UUID ticketId, UUID agentId) {
        log.info("[CUSTOMER SUPPORT] Assigning TicketId={} to AgentId={}", ticketId, agentId);

        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + ticketId));

        ticket.setAssignedAgentId(agentId);
        ticket.setStatus("ASSIGNED");
        return ticketRepository.save(ticket);
    }

    @Transactional
    public SupportTicket escalateTicket(UUID ticketId) {
        log.info("[CUSTOMER SUPPORT] Escalating TicketId={}", ticketId);

        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + ticketId));

        ticket.setStatus("ESCALATED");
        ticket.setPriority("CRITICAL");
        return ticketRepository.save(ticket);
    }

    @Transactional
    public SupportTicket resolveTicket(UUID ticketId) {
        log.info("[CUSTOMER SUPPORT] Resolving TicketId={}", ticketId);

        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + ticketId));

        ticket.setStatus("RESOLVED");
        return ticketRepository.save(ticket);
    }

    @Transactional
    public SupportTicketMessage sendMessage(UUID ticketId, UUID senderUserId, String senderRole, String content, Boolean isInternalNote, String attachments) {
        log.info("[CUSTOMER SUPPORT] Sending message TicketId={}, Sender={}, Role={}", ticketId, senderUserId, senderRole);

        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + ticketId));

        SupportTicketMessage msg = SupportTicketMessage.builder()
                .ticket(ticket)
                .senderUserId(senderUserId)
                .senderRole(senderRole != null ? senderRole : "CUSTOMER")
                .content(content)
                .isInternalNote(isInternalNote != null ? isInternalNote : false)
                .attachmentUrls(attachments)
                .build();

        return messageRepository.save(msg);
    }

    @Transactional
    public RefundCase requestRefund(String refundCode, UUID ticketId, UUID orderId, BigDecimal amount, String reason, String method, UUID tenantId) {
        log.info("[CUSTOMER SUPPORT] Refund request Code={}, Order={}, Amount={}", refundCode, orderId, amount);

        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + ticketId));

        RefundCase refund = refundRepository.findByRefundCode(refundCode).orElseGet(() ->
                RefundCase.builder()
                        .refundCode(refundCode)
                        .ticket(ticket)
                        .orderId(orderId)
                        .amount(amount)
                        .refundReason(reason != null ? reason : "WRONG_PRODUCT")
                        .refundMethod(method != null ? method : "WALLET")
                        .status("APPROVED")
                        .tenantId(tenantId)
                        .build()
        );

        return refundRepository.save(refund);
    }

    @Transactional
    public ReturnCase requestReturn(String returnCode, UUID ticketId, UUID orderId, UUID tenantId) {
        log.info("[CUSTOMER SUPPORT] Return request Code={}, Order={}", returnCode, orderId);

        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + ticketId));

        ReturnCase returnCase = returnRepository.findByReturnCode(returnCode).orElseGet(() ->
                ReturnCase.builder()
                        .returnCode(returnCode)
                        .ticket(ticket)
                        .orderId(orderId)
                        .pickupStatus("SCHEDULED")
                        .status("APPROVED")
                        .tenantId(tenantId)
                        .build()
        );

        return returnRepository.save(returnCase);
    }

    @Transactional
    public ReplacementCase requestReplacement(String replacementCode, UUID ticketId, UUID orderId, UUID tenantId) {
        log.info("[CUSTOMER SUPPORT] Replacement request Code={}, Order={}", replacementCode, orderId);

        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + ticketId));

        ReplacementCase replacement = replacementRepository.findByReplacementCode(replacementCode).orElseGet(() ->
                ReplacementCase.builder()
                        .replacementCode(replacementCode)
                        .ticket(ticket)
                        .orderId(orderId)
                        .dispatchStatus("PENDING_DISPATCH")
                        .status("APPROVED")
                        .tenantId(tenantId)
                        .build()
        );

        return replacementRepository.save(replacement);
    }

    @Transactional
    public CustomerCsatSurvey submitSurvey(UUID ticketId, UUID customerId, Integer csat, Integer nps, Integer ces, String feedback) {
        log.info("[CUSTOMER SUPPORT] CSAT Survey TicketId={}, CSAT={}, NPS={}", ticketId, csat, nps);

        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + ticketId));

        CustomerCsatSurvey survey = csatRepository.findByTicket_Id(ticketId).orElseGet(() ->
                CustomerCsatSurvey.builder()
                        .ticket(ticket)
                        .customerId(customerId)
                        .csatRating(csat != null ? csat : 5)
                        .npsScore(nps != null ? nps : 10)
                        .cesScore(ces != null ? ces : 5)
                        .feedback(feedback)
                        .build()
        );

        return csatRepository.save(survey);
    }

    @Transactional(readOnly = true)
    public List<SupportTicket> getTicketsByCustomer(UUID customerId) {
        return ticketRepository.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public List<SupportTicketMessage> getMessages(UUID ticketId) {
        return messageRepository.findByTicket_Id(ticketId);
    }

    @Transactional(readOnly = true)
    public List<KnowledgeArticle> getKnowledgeArticles(String category) {
        if (category != null) {
            return knowledgeRepository.findByCategory(category);
        }
        return knowledgeRepository.findAll();
    }
}
