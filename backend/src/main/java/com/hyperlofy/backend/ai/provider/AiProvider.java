package com.hyperlofy.backend.ai.provider;

public interface AiProvider {

    ProviderType providerType();

    String generate(ProviderType providerType, String prompt, String model, String systemPrompt);
}
