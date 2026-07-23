package com.hyperlofy.backend.ai.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperlofy.backend.ai.config.AiGatewayProperties;
import com.hyperlofy.backend.ai.dto.AiRequestDto;
import com.hyperlofy.backend.ai.dto.AiResponseDto;
import com.hyperlofy.backend.ai.exception.AiGatewayException;
import com.hyperlofy.backend.ai.exception.AiProviderException;
import com.hyperlofy.backend.ai.parser.AiResponseParser;
import com.hyperlofy.backend.ai.provider.AiProvider;
import com.hyperlofy.backend.ai.provider.AiProviderRegistry;
import com.hyperlofy.backend.ai.provider.ProviderType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.retry.support.RetryTemplate;

import java.time.OffsetDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiGatewayService {

    private final AiGatewayProperties properties;
    private final RetryTemplate aiGatewayRetryTemplate;
    private final ObjectMapper objectMapper;
    private final AiProviderRegistry aiProviderRegistry;
    private final AiResponseParser aiResponseParser;

    public AiResponseDto prompt(AiRequestDto request) {
        try {
            ProviderType providerType = resolveProvider(request.getProvider());
            String resolvedModel = request.getModel() != null ? request.getModel() : properties.getDefaultModel();
            String systemPrompt = request.getSystemPrompt() != null ? request.getSystemPrompt() : properties.getSystemPrompt();

            log.info("Processing AI prompt through gateway. provider={}, model={}", providerType, resolvedModel);

            AiProvider aiProvider = aiProviderRegistry.getProvider(providerType);

            String rawResponse = aiGatewayRetryTemplate.execute(context -> {
                try {
                    return aiProvider.generate(providerType, request.getPrompt(), resolvedModel, systemPrompt);
                } catch (Exception ex) {
                    log.warn("AI provider call failed. attempt={}, provider={}, model={}",
                            context.getRetryCount() + 1, providerType, resolvedModel, ex);
                    throw ex;
                }
            });

            Object parsed = aiResponseParser.parse(rawResponse);

            return AiResponseDto.builder()
                    .provider(providerType.name())
                    .model(resolvedModel)
                    .content(parsed)
                    .timestamp(OffsetDateTime.now())
                    .build();

        } catch (AiProviderException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("AI Gateway processing failed", ex);
            throw new AiGatewayException("AI gateway failed to process request", ex);
        }
    }

    private ProviderType resolveProvider(String provider) {
        if (provider == null || provider.isBlank()) {
            return ProviderType.valueOf(properties.getDefaultProvider().toUpperCase());
        }
        return ProviderType.valueOf(provider.toUpperCase());
    }
}
