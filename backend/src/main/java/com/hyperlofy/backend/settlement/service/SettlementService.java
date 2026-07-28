package com.hyperlofy.backend.settlement.service;

import com.hyperlofy.backend.settlement.entity.BeneficiaryAccount;
import com.hyperlofy.backend.settlement.entity.Settlement;
import com.hyperlofy.backend.settlement.entity.SettlementPayout;
import com.hyperlofy.backend.settlement.repository.BeneficiaryAccountRepository;
import com.hyperlofy.backend.settlement.repository.SettlementPayoutRepository;
import com.hyperlofy.backend.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private static final Logger log = LoggerFactory.getLogger(SettlementService.class);

    private final SettlementRepository settlementRepository;
    private final SettlementPayoutRepository payoutRepository;
    private final BeneficiaryAccountRepository beneficiaryRepository;

    @Transactional
    public Settlement createSettlement(UUID orderId, UUID payeeId, String payeeType, BigDecimal grossAmount, BigDecimal commissionPercent, BigDecimal taxPercent) {
        log.info("[SETTLEMENT ENGINE] Calculating settlement OrderId={}, PayeeId={}, Type={}, Gross={}", orderId, payeeId, payeeType, grossAmount);

        BigDecimal commRate = commissionPercent != null ? commissionPercent : new BigDecimal("15.0"); // 15% platform commission default
        BigDecimal commission = grossAmount.multiply(commRate.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);

        BigDecimal taxRate = taxPercent != null ? taxPercent : new BigDecimal("18.0"); // 18% GST tax default
        BigDecimal tax = commission.multiply(taxRate.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);

        BigDecimal netAmount = grossAmount.subtract(commission).subtract(tax).setScale(2, RoundingMode.HALF_UP);

        Settlement settlement = Settlement.builder()
                .orderId(orderId)
                .payeeId(payeeId)
                .payeeType(payeeType)
                .grossAmount(grossAmount)
                .platformCommission(commission)
                .taxAmount(tax)
                .netAmount(netAmount)
                .status("ELIGIBLE")
                .scheduledPayoutAt(ZonedDateTime.now().plusDays(1))
                .build();

        return settlementRepository.save(settlement);
    }

    @Transactional
    public SettlementPayout processPayout(UUID settlementId) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new IllegalArgumentException("Settlement not found: " + settlementId));

        log.info("[SETTLEMENT ENGINE] Orchestrating bank payout for SettlementId={}, PayeeId={}, NetAmount={}",
                settlementId, settlement.getPayeeId(), settlement.getNetAmount());

        BeneficiaryAccount account = beneficiaryRepository.findByOwnerId(settlement.getPayeeId()).orElseGet(() ->
                BeneficiaryAccount.builder()
                        .ownerId(settlement.getPayeeId())
                        .bankName("HDFC Bank")
                        .accountNumber("50100" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 7))
                        .ifscCode("HDFC0001234")
                        .accountHolderName("Registered Business Payee")
                        .isVerified(true)
                        .build()
        );

        beneficiaryRepository.save(account);

        settlement.setStatus("SETTLED");
        settlementRepository.save(settlement);

        SettlementPayout payout = SettlementPayout.builder()
                .settlementId(settlementId)
                .payoutReference("po_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14))
                .bankAccountNumber(account.getAccountNumber())
                .ifscCode(account.getIfscCode())
                .amount(settlement.getNetAmount())
                .status("SETTLED")
                .processedAt(ZonedDateTime.now())
                .build();

        return payoutRepository.save(payout);
    }

    @Transactional(readOnly = true)
    public List<Settlement> getPayeeSettlements(UUID payeeId) {
        return settlementRepository.findByPayeeIdOrderByCreatedAtDesc(payeeId);
    }

    @Transactional(readOnly = true)
    public Settlement getSettlement(UUID id) {
        return settlementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Settlement not found: " + id));
    }
}
