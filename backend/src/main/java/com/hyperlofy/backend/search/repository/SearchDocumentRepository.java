package com.hyperlofy.backend.search.repository;

import com.hyperlofy.backend.search.entity.SearchDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SearchDocumentRepository extends JpaRepository<SearchDocument, UUID> {
    List<SearchDocument> findByDocType(String docType);
    List<SearchDocument> findByTenantId(UUID tenantId);

    @Query("SELECT sd FROM SearchDocument sd WHERE LOWER(sd.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(sd.bodyExcerpt) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<SearchDocument> fullTextSearch(@Param("query") String query);
}
