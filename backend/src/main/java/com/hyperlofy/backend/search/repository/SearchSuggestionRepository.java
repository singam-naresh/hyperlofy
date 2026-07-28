package com.hyperlofy.backend.search.repository;

import com.hyperlofy.backend.search.entity.SearchSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SearchSuggestionRepository extends JpaRepository<SearchSuggestion, UUID> {
    List<SearchSuggestion> findBySuggestionTypeAndIsActiveTrue(String suggestionType);
    List<SearchSuggestion> findBySuggestionTextContainingIgnoreCase(String text);
}
