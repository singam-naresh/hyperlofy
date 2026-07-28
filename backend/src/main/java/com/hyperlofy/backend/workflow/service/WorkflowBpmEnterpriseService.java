package com.hyperlofy.backend.workflow.service;

import com.hyperlofy.backend.workflow.entity.*;
import com.hyperlofy.backend.workflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowBpmEnterpriseService {

    private static final Logger log = LoggerFactory.getLogger(WorkflowBpmEnterpriseService.class);

    private final WorkflowDefinitionRepository definitionRepository;
    private final WorkflowVersionRepository versionRepository;
    private final WorkflowCaseRepository caseRepository;
    private final WorkflowCaseNoteRepository caseNoteRepository;
    private final BusinessRuleRepository businessRuleRepository;
    private final WorkflowFormRepository formRepository;
    private final WorkflowEscalationPolicyRepository escalationPolicyRepository;
    private final WorkflowAnalyticsRepository analyticsRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1: BPM Version Management (DRAFT → ACTIVE → ARCHIVED)
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public WorkflowVersion deployDraftVersion(UUID definitionId, String bpmnXml, String versionNotes) {
        WorkflowDefinition definition = definitionRepository.findById(definitionId)
                .orElseThrow(() -> new IllegalArgumentException("Workflow definition not found: " + definitionId));

        int nextVersionNumber = versionRepository.findByDefinition_Id(definitionId).size() + 1;

        log.info("[BPM ENTERPRISE] Deploying DRAFT version DefinitionId={}, Version={}", definitionId, nextVersionNumber);

        WorkflowVersion version = WorkflowVersion.builder()
                .definition(definition)
                .versionNumber(nextVersionNumber)
                .versionStatus("DRAFT")
                .bpmnXml(bpmnXml)
                .versionNotes(versionNotes)
                .build();

        return versionRepository.save(version);
    }

    @Transactional
    public WorkflowVersion publishVersion(UUID versionId, UUID publishedBy) {
        WorkflowVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new IllegalArgumentException("Workflow version not found: " + versionId));

        log.info("[BPM ENTERPRISE] Publishing workflow version VersionId={}, Publisher={}", versionId, publishedBy);

        // Archive the current ACTIVE version if exists
        versionRepository.findByDefinition_IdAndVersionStatus(version.getDefinition().getId(), "ACTIVE")
                .ifPresent(activeVersion -> {
                    activeVersion.setVersionStatus("ARCHIVED");
                    activeVersion.setArchivedAt(OffsetDateTime.now());
                    versionRepository.save(activeVersion);
                    log.info("[BPM ENTERPRISE] Archived previous active version VersionId={}", activeVersion.getId());
                });

        version.setVersionStatus("ACTIVE");
        version.setPublishedBy(publishedBy);
        version.setPublishedAt(OffsetDateTime.now());
        return versionRepository.save(version);
    }

    @Transactional(readOnly = true)
    public List<WorkflowVersion> getVersionsByDefinition(UUID definitionId) {
        return versionRepository.findByDefinition_Id(definitionId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2: Adaptive Case Management (CMMN-inspired)
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public WorkflowCase openCase(String caseRef, String caseType, String title, String description,
            UUID subjectUserId, UUID assigneeUserId, UUID tenantId, String priority,
            UUID relatedWorkflowInstanceId) {

        log.info("[BPM ENTERPRISE] Opening case CaseRef={}, Type={}, Subject={}, Priority={}",
                caseRef, caseType, subjectUserId, priority);

        WorkflowCase workflowCase = WorkflowCase.builder()
                .caseRef(caseRef)
                .caseType(caseType)
                .title(title)
                .description(description)
                .subjectUserId(subjectUserId)
                .assigneeUserId(assigneeUserId)
                .tenantId(tenantId)
                .priority(priority != null ? priority : "NORMAL")
                .status("OPEN")
                .dueAt(OffsetDateTime.now().plusHours(72))
                .build();

        return caseRepository.save(workflowCase);
    }

    @Transactional
    public WorkflowCase closeCase(UUID caseId, String resolution) {
        WorkflowCase workflowCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Case not found: " + caseId));

        log.info("[BPM ENTERPRISE] Closing case CaseId={}, Resolution={}", caseId, resolution);

        workflowCase.setStatus("CLOSED");
        workflowCase.setClosedAt(OffsetDateTime.now());
        workflowCase.setResolution(resolution);
        return caseRepository.save(workflowCase);
    }

    @Transactional
    public WorkflowCaseNote addCaseNote(UUID caseId, UUID authorUserId, String noteType,
            String content, String attachmentUrl, boolean isInternal) {

        WorkflowCase workflowCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Case not found: " + caseId));

        log.info("[BPM ENTERPRISE] Adding case note CaseId={}, Author={}, Type={}", caseId, authorUserId, noteType);

        WorkflowCaseNote note = WorkflowCaseNote.builder()
                .workflowCase(workflowCase)
                .authorUserId(authorUserId)
                .noteType(noteType != null ? noteType : "NOTE")
                .content(content)
                .attachmentUrl(attachmentUrl)
                .isInternal(isInternal)
                .build();

        return caseNoteRepository.save(note);
    }

    @Transactional(readOnly = true)
    public List<WorkflowCase> getAllCases() {
        return caseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public WorkflowCase getCaseById(UUID caseId) {
        return caseRepository.findById(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Case not found: " + caseId));
    }

    @Transactional(readOnly = true)
    public List<WorkflowCaseNote> getCaseNotes(UUID caseId) {
        return caseNoteRepository.findByWorkflowCase_IdOrderByCreatedAtAsc(caseId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3: Business Rules Engine (DMN-inspired)
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public BusinessRule registerRule(String ruleKey, String ruleName, String ruleCategory,
            String conditionField, String conditionOperator,
            BigDecimal conditionValueMin, BigDecimal conditionValueMax,
            String actionType, String actionValue, Integer priority) {

        log.info("[BPM ENTERPRISE] Registering business rule RuleKey={}, Category={}, Action={}",
                ruleKey, ruleCategory, actionType);

        return businessRuleRepository.findByRuleKey(ruleKey).orElseGet(() -> {
            BusinessRule rule = BusinessRule.builder()
                    .ruleKey(ruleKey)
                    .ruleName(ruleName)
                    .ruleCategory(ruleCategory)
                    .conditionField(conditionField)
                    .conditionOperator(conditionOperator)
                    .conditionValueMin(conditionValueMin)
                    .conditionValueMax(conditionValueMax)
                    .actionType(actionType)
                    .actionValue(actionValue)
                    .priority(priority != null ? priority : 10)
                    .isActive(true)
                    .build();
            return businessRuleRepository.save(rule);
        });
    }

    /**
     * DMN-style rule evaluation — finds first matching rule by category and numeric value.
     * Supports LT, GT, LTE, GTE, EQ, BETWEEN operators.
     */
    @Transactional(readOnly = true)
    public BusinessRule evaluateRule(String ruleCategory, BigDecimal inputValue) {
        log.info("[BPM ENTERPRISE] Evaluating DMN rules Category={}, InputValue={}", ruleCategory, inputValue);

        List<BusinessRule> rules = businessRuleRepository
                .findByRuleCategoryAndIsActiveTrueOrderByPriorityAsc(ruleCategory);

        return rules.stream()
                .filter(rule -> matchesCondition(rule, inputValue))
                .findFirst()
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<BusinessRule> getAllRules() {
        return businessRuleRepository.findAll();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4: Dynamic Form Engine
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public WorkflowForm registerForm(String formKey, String formName, String formType, String formSchema) {
        log.info("[BPM ENTERPRISE] Registering dynamic form FormKey={}, Type={}", formKey, formType);

        return formRepository.findByFormKey(formKey).orElseGet(() -> {
            WorkflowForm form = WorkflowForm.builder()
                    .formKey(formKey)
                    .formName(formName)
                    .formType(formType != null ? formType : "TASK_FORM")
                    .formSchema(formSchema)
                    .version(1)
                    .isActive(true)
                    .build();
            return formRepository.save(form);
        });
    }

    @Transactional(readOnly = true)
    public WorkflowForm getFormByKey(String formKey) {
        return formRepository.findByFormKey(formKey)
                .orElseThrow(() -> new IllegalArgumentException("Form not found: " + formKey));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5: SLA & Escalation Policy Management
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public WorkflowEscalationPolicy registerEscalationPolicy(String policyName, String appliesToWorkflowType,
            Integer warningHours, Integer breachHours,
            String level1Group, String level2Group, String level3Group,
            Integer autoCancelHours) {

        log.info("[BPM ENTERPRISE] Registering SLA escalation policy Policy={}, Type={}, BreachHours={}",
                policyName, appliesToWorkflowType, breachHours);

        WorkflowEscalationPolicy policy = WorkflowEscalationPolicy.builder()
                .policyName(policyName)
                .appliesToWorkflowType(appliesToWorkflowType)
                .warningHours(warningHours != null ? warningHours : 24)
                .breachHours(breachHours != null ? breachHours : 48)
                .escalationLevel1Group(level1Group)
                .escalationLevel2Group(level2Group)
                .escalationLevel3Group(level3Group)
                .autoCancelHours(autoCancelHours)
                .isActive(true)
                .build();

        return escalationPolicyRepository.save(policy);
    }

    @Transactional(readOnly = true)
    public List<WorkflowEscalationPolicy> getActivePolicies() {
        return escalationPolicyRepository.findByIsActiveTrue();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 6: Process Analytics
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public WorkflowAnalytics recordAnalytics(String workflowType, LocalDate periodDate,
            Integer totalInstances, Integer completedInstances, Integer failedInstances,
            Integer compensatedInstances, BigDecimal avgExecutionHours,
            BigDecimal avgHumanApprovalHours, BigDecimal automationRatio, BigDecimal slaComplianceRate) {

        log.info("[BPM ENTERPRISE] Recording process analytics WorkflowType={}, Date={}, Total={}",
                workflowType, periodDate, totalInstances);

        WorkflowAnalytics analytics = analyticsRepository
                .findByWorkflowTypeAndPeriodDate(workflowType, periodDate)
                .orElse(WorkflowAnalytics.builder()
                        .workflowType(workflowType)
                        .periodDate(periodDate)
                        .build());

        analytics.setTotalInstances(totalInstances != null ? totalInstances : 0);
        analytics.setCompletedInstances(completedInstances != null ? completedInstances : 0);
        analytics.setFailedInstances(failedInstances != null ? failedInstances : 0);
        analytics.setCompensatedInstances(compensatedInstances != null ? compensatedInstances : 0);
        analytics.setAvgExecutionHours(avgExecutionHours != null ? avgExecutionHours : BigDecimal.ZERO);
        analytics.setAvgHumanApprovalHours(avgHumanApprovalHours != null ? avgHumanApprovalHours : BigDecimal.ZERO);
        analytics.setAutomationRatio(automationRatio != null ? automationRatio : BigDecimal.ZERO);
        analytics.setSlaComplianceRate(slaComplianceRate != null ? slaComplianceRate : new BigDecimal("100.00"));

        return analyticsRepository.save(analytics);
    }

    @Transactional(readOnly = true)
    public List<WorkflowAnalytics> getAnalyticsByType(String workflowType) {
        return analyticsRepository.findByWorkflowType(workflowType);
    }

    @Transactional(readOnly = true)
    public List<WorkflowAnalytics> getAllAnalytics() {
        return analyticsRepository.findAll();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private boolean matchesCondition(BusinessRule rule, BigDecimal inputValue) {
        if (inputValue == null) return false;
        BigDecimal min = rule.getConditionValueMin();
        BigDecimal max = rule.getConditionValueMax();
        return switch (rule.getConditionOperator()) {
            case "LT"      -> min != null && inputValue.compareTo(min) < 0;
            case "LTE"     -> min != null && inputValue.compareTo(min) <= 0;
            case "GT"      -> min != null && inputValue.compareTo(min) > 0;
            case "GTE"     -> min != null && inputValue.compareTo(min) >= 0;
            case "EQ"      -> min != null && inputValue.compareTo(min) == 0;
            case "BETWEEN" -> min != null && max != null
                    && inputValue.compareTo(min) >= 0
                    && inputValue.compareTo(max) <= 0;
            default        -> false;
        };
    }
}
