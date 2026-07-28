package com.hyperlofy.backend.search.repository;

import com.hyperlofy.backend.search.entity.KnowledgeArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface KnowledgeArticleRepository extends JpaRepository<KnowledgeArticle, UUID> {
    Optional<KnowledgeArticle> findByArticleKey(String articleKey);
    List<KnowledgeArticle> findByArticleType(String articleType);
    List<KnowledgeArticle> findByStatus(String status);
}
