package com.hyperlofy.backend.ai.genai.repository;

import com.hyperlofy.backend.ai.genai.entity.PromptTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PromptTemplateRepository extends JpaRepository<PromptTemplate, UUID> {
    Optional<PromptTemplate> findByTemplateKey(String templateKey);
}
