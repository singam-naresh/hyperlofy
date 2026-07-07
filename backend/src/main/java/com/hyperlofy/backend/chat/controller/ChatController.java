package com.hyperlofy.backend.chat.controller;

import com.hyperlofy.backend.chat.entity.ChatMessage;
import com.hyperlofy.backend.chat.service.ChatService;
import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<ChatMessage>> getOrderChatHistory(@PathVariable UUID orderId) {
        return ResponseEntity.ok(chatService.getChatHistory(orderId));
    }

    @PostMapping("/order/{orderId}/send")
    public ResponseEntity<ChatMessage> sendMessage(
            @PathVariable UUID orderId,
            @RequestParam String text) {
        User sender = getCurrentAuthenticatedUser();
        ChatMessage msg = chatService.saveAndPublishMessage(orderId, sender.getId(), text);
        return new ResponseEntity<>(msg, HttpStatus.CREATED);
    }

    private User getCurrentAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User profile not found", HttpStatus.UNAUTHORIZED));
    }
}
