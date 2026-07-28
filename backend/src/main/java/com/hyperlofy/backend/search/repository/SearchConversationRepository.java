package com.hyperlofy.backend.search.repository;

import com.hyperlofy.backend.search.entity.SearchConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SearchConversationRepository extends JpaRepository<SearchConversation, UUID> {
    Optional<SearchConversation> findByConversationCode(String conversationCode);
    List<SearchConversation> findByUserId(UUID userId);
}
