package com.hyperlofy.backend.ai.provider;

import com.hyperlofy.backend.ai.exception.AiProviderException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AiProviderRegistry {

    private final List<AiProvider> providers;

    public AiProvider getProvider(ProviderType providerType) {
        Map<ProviderType, AiProvider> providerMap = providers.stream()
                .collect(Collectors.toMap(AiProvider::providerType, Function.identity()));

        AiProvider provider = providerMap.get(providerType);
        if (provider == null) {
            throw new AiProviderException("Unsupported AI provider configured: " + providerType);
        }
        return provider;
    }
}
