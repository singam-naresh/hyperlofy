package com.hyperlofy.backend.ai.memory;

import com.hyperlofy.backend.ai.memory.dto.MemoryCreateRequest;
import com.hyperlofy.backend.ai.memory.dto.MemoryDto;
import com.hyperlofy.backend.ai.memory.dto.MemoryUpdateRequest;
import com.hyperlofy.backend.ai.conversation.ConversationResponse;
import com.hyperlofy.backend.ai.memory.repository.MemoryRepository;
import com.hyperlofy.backend.ai.orderbuilder.OrderDraft;
import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoryService {

    private final MemoryRepository memoryRepository;
    private final MemoryMapper memoryMapper;
    private final UserRepository userRepository;
    private final MemoryScoringService memoryScoringService;

    @Transactional
    public MemoryDto saveMemory(UUID customerId, MemoryCreateRequest request) {
        validateCustomer(customerId);

        String normalizedKey = request.getKey().trim().toLowerCase();
        MemoryEntity existing = memoryRepository.findByCustomerIdAndMemoryTypeAndKeyAndActiveTrue(
                customerId, request.getMemoryType(), normalizedKey).orElse(null);

        if (existing != null) {
            existing.setValue(request.getValue().trim());
            existing.setConfidence(request.getConfidence() == null ? existing.getConfidence() : request.getConfidence());
            existing.setUpdatedAt(OffsetDateTime.now());
            return memoryMapper.toDto(memoryRepository.save(existing));
        }

        MemoryEntity entity = memoryMapper.toEntity(customerId, request);
        return memoryMapper.toDto(memoryRepository.save(entity));
    }

    @Transactional
    public MemoryDto updateMemory(UUID customerId, UUID memoryId, MemoryUpdateRequest request) {
        validateCustomer(customerId);
        MemoryEntity entity = memoryRepository.findById(memoryId)
                .filter(memory -> memory.isActive() && memory.getCustomerId().equals(customerId))
                .orElseThrow(() -> new BusinessException("Memory not found", HttpStatus.NOT_FOUND));

        memoryMapper.updateEntity(entity, request);
        entity.setUpdatedAt(OffsetDateTime.now());
        return memoryMapper.toDto(memoryRepository.save(entity));
    }

    @Transactional
    public void deleteMemory(UUID customerId, UUID memoryId) {
        validateCustomer(customerId);
        MemoryEntity entity = memoryRepository.findById(memoryId)
                .filter(memory -> memory.getCustomerId().equals(customerId))
                .orElseThrow(() -> new BusinessException("Memory not found", HttpStatus.NOT_FOUND));
        entity.setActive(false);
        entity.setUpdatedAt(OffsetDateTime.now());
        memoryRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public List<MemoryDto> findCustomerMemory(UUID customerId) {
        validateCustomer(customerId);
        return memoryRepository.findByCustomerIdAndActiveTrueOrderByLastUsedAtDesc(customerId).stream()
                .map(memoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MemoryDto> findRelevantMemory(UUID customerId, OrderDraft draft) {
        validateCustomer(customerId);
        if (draft == null) {
            return List.of();
        }
        return memoryRepository.findByCustomerIdAndActiveTrueOrderByLastUsedAtDesc(customerId).stream()
                .map(memoryMapper::toDto)
                .map(memory -> new ScoredMemory(memory, memoryScoringService.scoreMemory(memory, draft)))
                .filter(scored -> scored.score() > 0.1)
                .sorted(Comparator.comparingDouble(ScoredMemory::score).reversed())
                .limit(10)
                .map(ScoredMemory::memory)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MemoryDto> findRelevantMemory(UUID customerId, ConversationResponse conversation) {
        validateCustomer(customerId);
        return memoryRepository.findByCustomerIdAndActiveTrueOrderByLastUsedAtDesc(customerId).stream()
                .map(memoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MemoryDto incrementUsage(UUID customerId, UUID memoryId) {
        validateCustomer(customerId);
        MemoryEntity entity = memoryRepository.findById(memoryId)
                .filter(memory -> memory.isActive() && memory.getCustomerId().equals(customerId))
                .orElseThrow(() -> new BusinessException("Memory not found", HttpStatus.NOT_FOUND));
        entity.setUsageCount(entity.getUsageCount() + 1);
        entity.setLastUsedAt(OffsetDateTime.now());
        return memoryMapper.toDto(memoryRepository.save(entity));
    }

    @Transactional
    public MemoryDto mergeDuplicateMemory(UUID customerId, UUID existingMemoryId, UUID duplicateMemoryId) {
        validateCustomer(customerId);
        MemoryEntity existing = memoryRepository.findById(existingMemoryId)
                .filter(memory -> memory.isActive() && memory.getCustomerId().equals(customerId))
                .orElseThrow(() -> new BusinessException("Existing memory not found", HttpStatus.NOT_FOUND));
        MemoryEntity duplicate = memoryRepository.findById(duplicateMemoryId)
                .filter(memory -> memory.isActive() && memory.getCustomerId().equals(customerId))
                .orElseThrow(() -> new BusinessException("Duplicate memory not found", HttpStatus.NOT_FOUND));

        existing.setValue(existing.getValue() + "; " + duplicate.getValue());
        existing.setConfidence(Math.max(existing.getConfidence(), duplicate.getConfidence()));
        existing.setUsageCount(existing.getUsageCount() + duplicate.getUsageCount());
        existing.setLastUsedAt(OffsetDateTime.now());
        duplicate.setActive(false);
        duplicate.setUpdatedAt(OffsetDateTime.now());

        memoryRepository.save(duplicate);
        return memoryMapper.toDto(memoryRepository.save(existing));
    }

    private void validateCustomer(UUID customerId) {
        if (customerId == null) {
            throw new BusinessException("Customer id is required", HttpStatus.BAD_REQUEST);
        }
        userRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException("Customer profile not found", HttpStatus.NOT_FOUND));
    }

    private record ScoredMemory(MemoryDto memory, double score) {}
}
