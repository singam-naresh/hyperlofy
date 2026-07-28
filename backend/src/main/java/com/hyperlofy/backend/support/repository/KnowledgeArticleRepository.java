package com.hyperlofy.backend.support.repository;

import com.hyperlofy.backend.support.entity.KnowledgeArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface KnowledgeArticleRepository extends JpaRepository<KnowledgeArticle, UUID> {
    Optional<KnowledgeArticle> findByArticleCode(String articleCode);
    List<KnowledgeArticle> findByCategory(String category);
}
