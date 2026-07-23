package com.hyperlofy.backend.ai.memory;

import com.hyperlofy.backend.ai.memory.dto.MemoryCreateRequest;
import com.hyperlofy.backend.ai.memory.dto.MemoryDto;
import com.hyperlofy.backend.ai.memory.dto.MemoryUpdateRequest;
import com.hyperlofy.backend.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/memories")
@RequiredArgsConstructor
public class MemoryController {

    private final MemoryService memoryService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<MemoryDto> createMemory(@Valid @RequestBody MemoryCreateRequest request) {
        UUID customerId = getAuthenticatedCustomerId();
        return ResponseEntity.ok(memoryService.saveMemory(customerId, request));
    }

    @PutMapping("/{memoryId}")
    public ResponseEntity<MemoryDto> updateMemory(@PathVariable UUID memoryId,
                                                   @Valid @RequestBody MemoryUpdateRequest request) {
        UUID customerId = getAuthenticatedCustomerId();
        return ResponseEntity.ok(memoryService.updateMemory(customerId, memoryId, request));
    }

    @DeleteMapping("/{memoryId}")
    public ResponseEntity<Void> deleteMemory(@PathVariable UUID memoryId) {
        UUID customerId = getAuthenticatedCustomerId();
        memoryService.deleteMemory(customerId, memoryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<MemoryDto>> getCustomerMemories() {
        UUID customerId = getAuthenticatedCustomerId();
        return ResponseEntity.ok(memoryService.findCustomerMemory(customerId));
    }

    @PostMapping("/{memoryId}/increment")
    public ResponseEntity<MemoryDto> incrementUsage(@PathVariable UUID memoryId) {
        UUID customerId = getAuthenticatedCustomerId();
        return ResponseEntity.ok(memoryService.incrementUsage(customerId, memoryId));
    }

    private UUID getAuthenticatedCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("Customer is not authenticated");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"))
                .getId();
    }
}
