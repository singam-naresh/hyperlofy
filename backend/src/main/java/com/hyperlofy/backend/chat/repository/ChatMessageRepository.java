package com.hyperlofy.backend.chat.repository;

import com.hyperlofy.backend.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findByOrderIdOrderByCreatedAtAsc(UUID orderId);
}
