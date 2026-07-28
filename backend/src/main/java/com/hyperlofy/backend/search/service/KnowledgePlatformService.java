package com.hyperlofy.backend.search.service;

import com.hyperlofy.backend.search.entity.KnowledgeArticle;
import com.hyperlofy.backend.search.entity.KnowledgeCategory;
import com.hyperlofy.backend.search.repository.KnowledgeArticleRepository;
import com.hyperlofy.backend.search.repository.KnowledgeCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KnowledgePlatformService {

    private static final Logger log = LoggerFactory.getLogger(KnowledgePlatformService.class);

    private final KnowledgeArticleRepository articleRepository;
    private final KnowledgeCategoryRepository categoryRepository;

    @Transactional
    public KnowledgeArticle createArticle(String articleKey, String title, String articleType,
                                          String content, String summary, UUID categoryId,
                                          UUID authorUserId, String tags, UUID tenantId) {
        log.info("[KNOWLEDGE PLATFORM] Creating knowledge article Key={}, Type={}, Title={}", articleKey, articleType, title);

        KnowledgeArticle article = KnowledgeArticle.builder()
                .articleKey(articleKey)
                .title(title)
                .articleType(articleType)
                .content(content)
                .contentSummary(summary)
                .categoryId(categoryId)
                .authorUserId(authorUserId)
                .tags(tags)
                .tenantId(tenantId)
                .status("DRAFT")
                .version(1)
                .viewCount(0)
                .helpfulVotes(0)
                .build();

        return articleRepository.save(article);
    }

    @Transactional
    public KnowledgeArticle publishArticle(UUID articleId, UUID reviewerUserId) {
        log.info("[KNOWLEDGE PLATFORM] Publishing article ArticleId={}, Reviewer={}", articleId, reviewerUserId);

        KnowledgeArticle article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Knowledge article not found: " + articleId));

        article.setStatus("PUBLISHED");
        article.setReviewerUserId(reviewerUserId);
        article.setPublishedAt(OffsetDateTime.now());

        return articleRepository.save(article);
    }

    @Transactional(readOnly = true)
    public KnowledgeArticle getArticle(UUID articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Knowledge article not found: " + articleId));
    }

    @Transactional(readOnly = true)
    public List<KnowledgeArticle> getAllArticles() {
        return articleRepository.findAll();
    }

    @Transactional
    public KnowledgeCategory createCategory(String categoryKey, String categoryName, UUID parentId, String icon) {
        log.info("[KNOWLEDGE PLATFORM] Creating category Key={}, Name={}", categoryKey, categoryName);

        KnowledgeCategory category = KnowledgeCategory.builder()
                .categoryKey(categoryKey)
                .categoryName(categoryName)
                .parentId(parentId)
                .icon(icon)
                .sortOrder(0)
                .articleCount(0)
                .isActive(true)
                .build();

        return categoryRepository.save(category);
    }
}
