package com.hyperlofy.backend.ai.platform.service;

import com.hyperlofy.backend.ai.platform.entity.AiInferenceLog;
import com.hyperlofy.backend.ai.platform.entity.AiModelRegistry;
import com.hyperlofy.backend.ai.platform.entity.AiPrompt;
import com.hyperlofy.backend.ai.platform.entity.AiRecommendation;
import com.hyperlofy.backend.ai.platform.repository.AiInferenceLogRepository;
import com.hyperlofy.backend.ai.platform.repository.AiModelRegistryRepository;
import com.hyperlofy.backend.ai.platform.repository.AiPromptRepository;
import com.hyperlofy.backend.ai.platform.repository.AiRecommendationRepository;
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
public class AiPlatformService {

    private static final Logger log = LoggerFactory.getLogger(AiPlatformService.class);

    private final AiPromptRepository promptRepository;
    private final AiModelRegistryRepository modelRepository;
    private final AiRecommendationRepository recommendationRepository;
    private final AiInferenceLogRepository inferenceLogRepository;

    @Transactional
    public AiPrompt registerPromptTemplate(String promptKey, String promptName, String templateText) {
        log.info("[AI PLATFORM] Registering prompt template Key={}, Name={}", promptKey, promptName);

        AiPrompt prompt = promptRepository.findByPromptKey(promptKey).orElseGet(() ->
                AiPrompt.builder()
                        .promptKey(promptKey)
                        .promptName(promptName)
                        .templateText(templateText)
                        .version("v1.0.0")
                        .isActive(true)
                        .build()
        );

        prompt.setTemplateText(templateText);
        return promptRepository.save(prompt);
    }

    @Transactional
    public AiRecommendation generateRecommendation(UUID userId, String type, UUID entityId, BigDecimal confidence) {
        log.info("[AI PLATFORM] Generating personalized recommendation UserId={}, Type={}, EntityId={}, Confidence={}",
                userId, type, entityId, confidence);

        AiRecommendation recommendation = AiRecommendation.builder()
                .userId(userId)
                .recommendationType(type)
                .recommendedEntityId(entityId)
                .confidenceScore(confidence != null ? confidence : new BigDecimal("0.9200"))
                .build();

        return recommendationRepository.save(recommendation);
    }

    @Transactional
    public AiInferenceLog recordInference(String modelName, String promptKey, UUID userId, Integer tokenCount, Integer executionMs) {
        log.info("[AI PLATFORM] Logging inference execution Model={}, PromptKey={}, User={}, Latency={}ms",
                modelName, promptKey, userId, executionMs);

        AiInferenceLog inferenceLog = AiInferenceLog.builder()
                .modelName(modelName)
                .promptKey(promptKey)
                .userId(userId)
                .tokenCount(tokenCount != null ? tokenCount : 256)
                .executionTimeMs(executionMs != null ? executionMs : 145)
                .status("SUCCESS")
                .build();

        return inferenceLogRepository.save(inferenceLog);
    }

    @Transactional(readOnly = true)
    public List<AiModelRegistry> getRegisteredModels() {
        return modelRepository.findAll();
    }
}
