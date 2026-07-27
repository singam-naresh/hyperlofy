package com.hyperlofy.backend.ai.fraud.repository;

import com.hyperlofy.backend.ai.fraud.entity.FraudRuleConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FraudRuleConfigRepository extends JpaRepository<FraudRuleConfig, UUID> {
    Optional<FraudRuleConfig> findByRuleName(String ruleName);
}
