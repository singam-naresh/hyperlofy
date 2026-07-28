package com.hyperlofy.backend.platform.service;

import com.hyperlofy.backend.platform.entity.FeatureFlag;
import com.hyperlofy.backend.platform.entity.SecretRotationHistory;
import com.hyperlofy.backend.platform.entity.SupportedLanguage;
import com.hyperlofy.backend.platform.repository.FeatureFlagRepository;
import com.hyperlofy.backend.platform.repository.SecretRotationHistoryRepository;
import com.hyperlofy.backend.platform.repository.SupportedLanguageRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlatformFoundationPart2Service {

    private static final Logger log = LoggerFactory.getLogger(PlatformFoundationPart2Service.class);

    private final SupportedLanguageRepository languageRepository;
    private final FeatureFlagRepository featureFlagRepository;
    private final SecretRotationHistoryRepository secretRotationRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "supported_languages", key = "'active_languages'")
    public List<SupportedLanguage> getActiveSupportedLanguages() {
        return languageRepository.findByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "feature_flags", key = "#flagKey")
    public boolean isFeatureEnabled(String flagKey) {
        return featureFlagRepository.findByFlagKey(flagKey)
                .map(FeatureFlag::getIsEnabled)
                .orElse(true);
    }

    @Transactional
    @CacheEvict(value = "feature_flags", key = "#flagKey")
    public FeatureFlag updateFeatureFlag(String flagKey, boolean isEnabled, int rolloutPercentage) {
        log.info("Updating feature flag: key={}, enabled={}, percentage={}", flagKey, isEnabled, rolloutPercentage);
        FeatureFlag flag = featureFlagRepository.findByFlagKey(flagKey).orElseGet(() ->
                FeatureFlag.builder()
                        .flagKey(flagKey)
                        .description("Dynamic platform feature flag")
                        .build()
        );
        flag.setIsEnabled(isEnabled);
        flag.setRolloutPercentage(rolloutPercentage);
        return featureFlagRepository.save(flag);
    }

    @Transactional
    public SecretRotationHistory recordSecretRotation(String secretKey, String versionId, String provider, String rotatedBy) {
        log.info("Recording secret key rotation: key={}, version={}", secretKey, versionId);
        SecretRotationHistory history = SecretRotationHistory.builder()
                .secretKey(secretKey)
                .versionId(versionId)
                .providerName(provider)
                .rotatedBy(rotatedBy)
                .build();
        return secretRotationRepository.save(history);
    }
}
