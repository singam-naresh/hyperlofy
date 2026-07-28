package com.hyperlofy.backend.ai.platform.service;

import com.hyperlofy.backend.ai.platform.entity.AiAgentRun;
import com.hyperlofy.backend.ai.platform.entity.AiGovernance;
import com.hyperlofy.backend.ai.platform.entity.AiMemoryStore;
import com.hyperlofy.backend.ai.platform.entity.AiSafetyEvent;
import com.hyperlofy.backend.ai.platform.repository.AiAgentRunRepository;
import com.hyperlofy.backend.ai.platform.repository.AiGovernanceRepository;
import com.hyperlofy.backend.ai.platform.repository.AiMemoryStoreRepository;
import com.hyperlofy.backend.ai.platform.repository.AiSafetyEventRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiEnterpriseService {

    private static final Logger log = LoggerFactory.getLogger(AiEnterpriseService.class);

    private final AiGovernanceRepository governanceRepository;
    private final AiSafetyEventRepository safetyRepository;
    private final AiAgentRunRepository agentRunRepository;
    private final AiMemoryStoreRepository memoryRepository;

    @Transactional
    public AiGovernance approveModel(String modelName, String approvedBy, String policyVersion) {
        log.info("[AI ENTERPRISE] Approving LLM model for production use Model={}, ApprovedBy={}", modelName, approvedBy);

        AiGovernance governance = governanceRepository.findByModelName(modelName).orElseGet(() ->
                AiGovernance.builder()
                        .modelName(modelName)
                        .approvedBy(approvedBy)
                        .policyVersion(policyVersion != null ? policyVersion : "v1.0.0")
                        .build()
        );

        governance.setApprovalStatus("APPROVED");
        governance.setApprovedBy(approvedBy);
        return governanceRepository.save(governance);
    }

    @Transactional
    public AiSafetyEvent recordSafetyViolation(UUID userId, String violationType, String severity, String sanitizedPrompt) {
        log.info("[AI ENTERPRISE] Recording AI safety violation User={}, Type={}, Severity={}", userId, violationType, severity);

        AiSafetyEvent safetyEvent = AiSafetyEvent.builder()
                .userId(userId)
                .violationType(violationType)
                .severity(severity != null ? severity : "MEDIUM")
                .sanitizedPrompt(sanitizedPrompt)
                .actionTaken("BLOCKED")
                .build();

        return safetyRepository.save(safetyEvent);
    }

    @Transactional
    public AiAgentRun recordAgentExecution(String agentName, String taskGoal, String stepsJson) {
        log.info("[AI ENTERPRISE] Logging autonomous agent workflow execution Agent={}, Goal={}", agentName, taskGoal);

        AiAgentRun run = AiAgentRun.builder()
                .agentName(agentName)
                .taskGoal(taskGoal)
                .status("COMPLETED")
                .executionStepsJson(stepsJson)
                .build();

        return agentRunRepository.save(run);
    }

    @Transactional
    public AiMemoryStore storeLongTermMemory(UUID userId, String key, String value, String memoryType) {
        log.info("[AI ENTERPRISE] Storing long-term user memory User={}, Key={}, Type={}", userId, key, memoryType);

        AiMemoryStore memory = AiMemoryStore.builder()
                .userId(userId)
                .memoryKey(key)
                .memoryValue(value)
                .memoryType(memoryType != null ? memoryType : "CONVERSATION")
                .build();

        return memoryRepository.save(memory);
    }
}
