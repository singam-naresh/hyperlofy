package com.hyperlofy.backend.platform.repository;

import com.hyperlofy.backend.platform.entity.SupportedLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupportedLanguageRepository extends JpaRepository<SupportedLanguage, UUID> {
    Optional<SupportedLanguage> findByLanguageCode(String languageCode);
    List<SupportedLanguage> findByIsActiveTrue();
}
