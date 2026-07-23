package com.hyperlofy.backend.ai.learning;

import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerPreferenceService {

    private final LearningAnalyzer learningAnalyzer;
    private final UserRepository userRepository;

    public List<PreferenceScoreDto> getCustomerPreferences(UUID customerId) {
        validateCustomer(customerId);
        return learningAnalyzer.analyzePreferences(customerId);
    }

    private void validateCustomer(UUID customerId) {
        if (customerId == null) {
            throw new com.hyperlofy.backend.common.exception.BusinessException("Customer id is required", HttpStatus.BAD_REQUEST);
        }
        userRepository.findById(customerId)
                .orElseThrow(() -> new com.hyperlofy.backend.common.exception.BusinessException("Customer profile not found", HttpStatus.NOT_FOUND));
    }
}
