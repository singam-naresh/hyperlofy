package com.hyperlofy.backend.search.repository;

import com.hyperlofy.backend.search.entity.KnowledgeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface KnowledgeCategoryRepository extends JpaRepository<KnowledgeCategory, UUID> {
    Optional<KnowledgeCategory> findByCategoryKey(String categoryKey);
}
