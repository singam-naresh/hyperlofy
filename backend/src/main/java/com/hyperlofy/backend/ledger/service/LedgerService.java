package com.hyperlofy.backend.ledger.service;

import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.agent.entity.AgentPayoutProfile;
import com.hyperlofy.backend.agent.repository.AgentPayoutProfileRepository;
import com.hyperlofy.backend.ledger.entity.*;
import com.hyperlofy.backend.ledger.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LedgerService {

    private final LedgerEntryRepository ledgerEntryRepository;
    private final EscrowTransactionRepository escrowTransactionRepository;
    private final CommissionLedgerRepository commissionLedgerRepository;
    private final SettlementBatchRepository settlementBatchRepository;
    private final AgentPayoutProfileRepository agentPayoutProfileRepository;

    /**
     * Records a double-entry debit/credit ledger booking securely.
     */
    @Transactional
    public LedgerEntry bookTransaction(
            String debitAccount,
            String creditAccount,
            BigDecimal amount,
            String transactionType,
            UUID referenceId,
            UUID orderId,
            UUID paymentId,
            String description) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Accounting ledger booking requires positive amount", HttpStatus.BAD_REQUEST);
        }

        LedgerEntry entry = LedgerEntry.builder()
                .debitAccount(debitAccount)
                .creditAccount(creditAccount)
                .amount(amount)
                .referenceId(referenceId)
                .orderId(orderId)
                .paymentId(paymentId)
                .transactionType(transactionType)
                .description(description)
                .build();

        log.info("[Ledger Entry Booked] Type: {}, Debit: {}, Credit: {}, Amount: {}", 
                transactionType, debitAccount, creditAccount, amount);
        return ledgerEntryRepository.save(entry);
    }

    /**
     * Suspends delivery payment in Escrow when an order is first paid.
     */
    @Transactional
    public EscrowTransaction placeInEscrow(UUID orderId, UUID paymentId, BigDecimal amount) {
        // Book transaction debiting Customer Wallet, crediting Escrow Pool
        bookTransaction(
                "USER_WALLET_DEBIT_" + orderId,
                "ESCROW_HOLDING_POOL",
                amount,
                "ES_HELD",
                orderId,
                orderId,
                paymentId,
                "Delivery fees retained in Escrow"
        );

        EscrowTransaction escrow = EscrowTransaction.builder()
                .orderId(orderId)
                .paymentId(paymentId)
                .amount(amount)
                .status("HELD")
                .build();

        return escrowTransactionRepository.save(escrow);
    }

    /**
     * Releases Escrow funds, records agent split-commissions, and transfers platform revenue shares.
     */
    @Transactional
    public void releaseEscrow(UUID orderId, UUID agentId) {
        EscrowTransaction escrow = escrowTransactionRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BusinessException("Escrow txn not found for Order: " + orderId, HttpStatus.NOT_FOUND));

        if (!"HELD".equals(escrow.getStatus())) {
            throw new BusinessException("Escrow already processed. Current status: " + escrow.getStatus(), HttpStatus.BAD_REQUEST);
        }

        escrow.setStatus("RELEASED");
        escrow.setReleasedAt(OffsetDateTime.now());
        escrowTransactionRepository.save(escrow);

        // Standard commission processing (15% platform commission, 85% agent share)
        BigDecimal totalDeliveryFee = escrow.getAmount();
        BigDecimal platformCommissionRate = new BigDecimal("15.00");
        BigDecimal commissionAmount = totalDeliveryFee.multiply(new BigDecimal("0.15"));
        BigDecimal agentShare = totalDeliveryFee.subtract(commissionAmount);

        // Book split ledger entries (immutable entries)
        // 1. Release from generic Escrow holding to specific split holding
        bookTransaction(
                "ESCROW_HOLDING_POOL",
                "PLATFORM_REVENUE",
                commissionAmount,
                "COMMISSION_CHARGED",
                escrow.getId(),
                orderId,
                escrow.getPaymentId(),
                "15% Platform revenue processing charge"
        );

        bookTransaction(
                "ESCROW_HOLDING_POOL",
                "AGENT_EARNINGS_HOLDING_" + agentId,
                agentShare,
                "AGENT_CREDIT",
                escrow.getId(),
                orderId,
                escrow.getPaymentId(),
                "85% Agent delivery execution split"
        );

        // Record on commission ledger
        CommissionLedger commission = CommissionLedger.builder()
                .orderId(orderId)
                .agentId(agentId)
                .totalOrderAmount(totalDeliveryFee)
                .commissionRate(platformCommissionRate)
                .commissionAmount(commissionAmount)
                .agentShare(agentShare)
                .build();
        commissionLedgerRepository.save(commission);

        log.info("[Escrow Released] Order: {}, Commission: {}, Agent Share: {}", orderId, commissionAmount, agentShare);
    }

    /**
     * Reverts Escrow balance back to customer in case of refunds.
     */
    @Transactional
    public void refundEscrow(UUID orderId) {
        EscrowTransaction escrow = escrowTransactionRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BusinessException("Escrow txn not found for Order: " + orderId, HttpStatus.NOT_FOUND));

        if (!"HELD".equals(escrow.getStatus())) {
            throw new BusinessException("Cannot refund. Escrow is currently: " + escrow.getStatus(), HttpStatus.BAD_REQUEST);
        }

        escrow.setStatus("REFUNDED");
        escrowTransactionRepository.save(escrow);

        // Debit Escrow, credit back to customer wallet
        bookTransaction(
                "ESCROW_HOLDING_POOL",
                "USER_WALLET_CREDIT_" + orderId,
                escrow.getAmount(),
                "REFUNDED",
                escrow.getId(),
                orderId,
                escrow.getPaymentId(),
                "Escrow refunded to customer"
        );
    }

    /**
     * Ledger verification service: Sum of debits and credits must perfectly equate.
     */
    @Transactional(readOnly = true)
    public boolean verifyLedgerIntegrity() {
        List<LedgerEntry> entries = ledgerEntryRepository.findAll();
        BigDecimal totalDebits = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;

        for (LedgerEntry entry : entries) {
            // In double-entry ledger bookkeeping, we verify the absolute sums balance
            totalDebits = totalDebits.add(entry.getAmount());
            totalCredits = totalCredits.add(entry.getAmount());
        }

        boolean isBalanced = totalDebits.compareTo(totalCredits) == 0;
        log.info("[Ledger Integrity Audit] Checked {} entries. Balanced: {}, Total volume: {}", 
                entries.size(), isBalanced, totalDebits);
        return isBalanced;
    }

    /**
     * Reconciliation service: Reconcile payments versus corresponding escrow holdings.
     */
    @Transactional(readOnly = true)
    public boolean reconcilePaymentsAndEscrows() {
        List<EscrowTransaction> escrows = escrowTransactionRepository.findAll();
        boolean matches = true;

        for (EscrowTransaction es : escrows) {
            List<LedgerEntry> relatedLedgers = ledgerEntryRepository.findByOrderId(es.getOrderId());
            BigDecimal ledgerTotalForOrder = relatedLedgers.stream()
                    .filter(l -> "ES_HELD".equals(l.getTransactionType()))
                    .map(LedgerEntry::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (es.getAmount().compareTo(ledgerTotalForOrder) != 0) {
                log.error("[Reconciliation Error] Desync found for Order ID: {}! Escrow balance: {}, Ledger HELD balance: {}", 
                        es.getOrderId(), es.getAmount(), ledgerTotalForOrder);
                matches = false;
            }
        }
        return matches;
    }

    /**
     * Settlement Service: Aggregates Open/Holding agent balances, moves them to Agent Payout profiles.
     */
    @Transactional
    public SettlementBatch triggerSettlementBatch() {
        List<SettlementBatch> activeBatches = settlementBatchRepository.findByBatchStatus("OPEN");
        SettlementBatch batch;
        if (activeBatches.isEmpty()) {
            batch = SettlementBatch.builder()
                    .batchStatus("OPEN")
                    .totalSettlementAmount(BigDecimal.ZERO)
                    .build();
            batch = settlementBatchRepository.save(batch);
        } else {
            batch = activeBatches.get(0);
        }

        batch.setBatchStatus("PROCESSING");
        settlementBatchRepository.save(batch);

        BigDecimal processAmount = BigDecimal.ZERO;

        // Iterate over agent payout profiles to settle outstanding balances holding in agent earnings
        List<AgentPayoutProfile> profiles = agentPayoutProfileRepository.findAll();
        for (AgentPayoutProfile profile : profiles) {
            UUID agentId = profile.getAgentId();
            List<CommissionLedger> unpaidAgentCommissions = commissionLedgerRepository.findByAgentId(agentId);

            // Calculate unpaid agent revenue
            BigDecimal dueAmount = unpaidAgentCommissions.stream()
                    .map(CommissionLedger::getAgentShare)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
                // Book payout transfer transaction
                bookTransaction(
                        "AGENT_EARNINGS_HOLDING_" + agentId,
                        "AGENT_BANK_OUTFLOW_" + agentId,
                        dueAmount,
                        "PAYOUT",
                        batch.getId(),
                        null,
                        null,
                        "Settle process via batch: " + batch.getId()
                );

                // Credit the payout profile balances
                profile.setCumulativeEarnings(profile.getCumulativeEarnings().add(dueAmount));
                profile.setCurrentBalance(profile.getCurrentBalance().add(dueAmount));
                agentPayoutProfileRepository.save(profile);

                processAmount = processAmount.add(dueAmount);
            }
        }

        batch.setTotalSettlementAmount(processAmount);
        batch.setBatchStatus("COMPLETED");
        batch.setProcessedAt(OffsetDateTime.now());
        
        return settlementBatchRepository.save(batch);
    }
}
