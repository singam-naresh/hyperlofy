package com.hyperlofy.backend.settlement.service;

import com.hyperlofy.backend.settlement.entity.SettlementBankRoute;
import com.hyperlofy.backend.settlement.entity.SettlementGovernance;
import com.hyperlofy.backend.settlement.entity.SettlementRiskEvent;
import com.hyperlofy.backend.settlement.entity.SettlementTreasury;
import com.hyperlofy.backend.settlement.repository.SettlementBankRouteRepository;
import com.hyperlofy.backend.settlement.repository.SettlementGovernanceRepository;
import com.hyperlofy.backend.settlement.repository.SettlementRiskEventRepository;
import com.hyperlofy.backend.settlement.repository.SettlementTreasuryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettlementEnterpriseService {

    private static final Logger log = LoggerFactory.getLogger(SettlementEnterpriseService.class);

    private final SettlementTreasuryRepository treasuryRepository;
    private final SettlementBankRouteRepository routeRepository;
    private final SettlementRiskEventRepository riskRepository;
    private final SettlementGovernanceRepository governanceRepository;

    @Transactional(readOnly = true)
    public SettlementTreasury getTreasuryPosition(String reservePoolName) {
        log.info("[SETTLEMENT ENTERPRISE] Fetching treasury liquidity position for ReservePool={}", reservePoolName);

        return treasuryRepository.findByReservePoolName(reservePoolName)
                .orElseGet(() -> {
                    SettlementTreasury t = SettlementTreasury.builder()
                            .reservePoolName(reservePoolName)
                            .availableLiquidity(new BigDecimal("50000000.00"))
                            .lockedEscrow(BigDecimal.ZERO)
                            .build();
                    return treasuryRepository.save(t);
                });
    }

    @Transactional(readOnly = true)
    public SettlementBankRoute selectOptimalPayoutRoute() {
        List<SettlementBankRoute> routes = routeRepository.findByIsActiveTrueOrderByPriorityOrderAsc();
        if (routes.isEmpty()) {
            return SettlementBankRoute.builder()
                    .gatewayName("RAZORPAY_X")
                    .priorityOrder(1)
                    .isActive(true)
                    .successRate(new BigDecimal("99.50"))
                    .avgLatencyMs(120)
                    .build();
        }
        return routes.get(0);
    }

    @Transactional
    public SettlementRiskEvent evaluatePayoutRisk(UUID settlementId, BigDecimal netAmount) {
        log.info("[SETTLEMENT ENTERPRISE] Evaluating payout risk score for SettlementId={}, NetAmount={}", settlementId, netAmount);

        BigDecimal riskScore = new BigDecimal("10.00");
        String riskType = "LOW_RISK";
        String actionTaken = "AUTO_APPROVED";

        if (netAmount.compareTo(new BigDecimal("100000.00")) > 0) {
            riskScore = new BigDecimal("85.00");
            riskType = "HIGH_VALUE_TRANSFER";
            actionTaken = "REQUIRES_DUAL_APPROVAL";
        }

        SettlementRiskEvent risk = SettlementRiskEvent.builder()
                .settlementId(settlementId)
                .riskType(riskType)
                .riskScore(riskScore)
                .actionTaken(actionTaken)
                .build();

        return riskRepository.save(risk);
    }

    @Transactional
    public SettlementGovernance requestGovernanceApproval(UUID settlementId, String requestedBy, String notes) {
        log.info("[SETTLEMENT ENTERPRISE] Requesting dual approval governance for SettlementId={}", settlementId);

        SettlementGovernance governance = SettlementGovernance.builder()
                .settlementId(settlementId)
                .requestedBy(requestedBy)
                .status("PENDING_APPROVAL")
                .notes(notes)
                .build();

        return governanceRepository.save(governance);
    }
}
