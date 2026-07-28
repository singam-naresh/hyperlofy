package com.hyperlofy.backend.search.repository;

import com.hyperlofy.backend.search.entity.SearchIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SearchIndexRepository extends JpaRepository<SearchIndex, UUID> {
    Optional<SearchIndex> findByIndexName(String indexName);
    List<SearchIndex> findByDomain(String domain);
}
