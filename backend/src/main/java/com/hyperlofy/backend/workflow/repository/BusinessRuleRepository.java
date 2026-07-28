package com.hyperlofy.backend.workflow.repository;

import com.hyperlofy.backend.workflow.entity.BusinessRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BusinessRuleRepository extends JpaRepository<BusinessRule, UUID> {
    Optional<BusinessRule> findByRuleKey(String ruleKey);
    List<BusinessRule> findByRuleCategoryAndIsActiveTrueOrderByPriorityAsc(String ruleCategory);
}
