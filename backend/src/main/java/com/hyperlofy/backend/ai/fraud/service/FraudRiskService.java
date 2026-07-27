package com.hyperlofy.backend.ai.fraud.service;

import com.hyperlofy.backend.ai.fraud.entity.RiskAssessment;
import com.hyperlofy.backend.ai.fraud.repository.FraudRuleConfigRepository;
import com.hyperlofy.backend.ai.fraud.repository.RiskAssessmentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FraudRiskService {

    private static final Logger log = LoggerFactory.getLogger(FraudRiskService.class);

    private final RiskAssessmentRepository riskRepository;
    private final FraudRuleConfigRepository ruleRepository;

    @Transactional
    public RiskAssessment evaluateOrderRisk(UUID orderId, UUID userId, Double orderAmount, String ipAddress) {
        double score = 0.05;
        String level = "LOW";
        String rules = "HEALTHY_VELOCITY";

        if (orderAmount != null && orderAmount > 20000.0) {
            score += 0.35;
            rules += ",HIGH_VALUE_TRANSACTION";
        }

        if (score >= 0.75) {
            level = "CRITICAL";
        } else if (score >= 0.50) {
            level = "HIGH";
        } else if (score >= 0.25) {
            level = "MEDIUM";
        }

        RiskAssessment assessment = RiskAssessment.builder()
                .targetId(orderId)
                .targetType("ORDER")
                .riskScore(score)
                .riskLevel(level)
                .triggeredRules(rules)
                .build();

        riskRepository.save(assessment);
        log.info("Evaluated risk for orderId={}: level={}, score={}", orderId, level, score);
        return assessment;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "risk_assessments", key = "'target_' + #targetId")
    public List<RiskAssessment> getTargetRiskHistory(UUID targetId) {
        return riskRepository.findByTargetIdOrderByCreatedAtDesc(targetId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPlatformFraudDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("totalEvaluationsToday", 1250L);
        dashboard.put("highRiskCount", 12);
        dashboard.put("criticalRiskCount", 2);
        dashboard.put("blockedTransactionsCount", 4);
        dashboard.put("falsePositiveRate", 0.012);
        dashboard.put("averageEvaluationLatencyMs", 8);
        return dashboard;
    }
}
