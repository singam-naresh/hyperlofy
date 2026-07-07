package com.hyperlofy.backend.chat.service;

import com.hyperlofy.backend.chat.entity.ChatMessage;
import com.hyperlofy.backend.chat.repository.ChatMessageRepository;
import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public ChatMessage saveAndPublishMessage(UUID orderId, UUID senderId, String text) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new BusinessException("Sender profile not found", HttpStatus.NOT_FOUND));

        ChatMessage message = ChatMessage.builder()
                .orderId(orderId)
                .sender(sender)
                .messageText(text)
                .build();

        ChatMessage saved = chatMessageRepository.save(message);
        
        // Push over WebSocket topic dynamically
        String destination = "/topic/orders/" + orderId + "/chat";
        try {
            messagingTemplate.convertAndSend(destination, saved);
            log.info("ChatMessage dispatched over STOMP channel: {}", destination);
        } catch (Exception e) {
            log.warn("Websocket was unavailable or not fully configured, fall back gracefully: {}", e.getMessage());
        }

        return saved;
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getChatHistory(UUID orderId) {
        return chatMessageRepository.findByOrderIdOrderByCreatedAtAsc(orderId);
    }
}
