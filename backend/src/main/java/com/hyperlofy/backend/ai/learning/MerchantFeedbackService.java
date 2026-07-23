package com.hyperlofy.backend.ai.learning;

import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.user.entity.Role;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MerchantFeedbackService {

    private final LearningAnalyzer learningAnalyzer;
    private final UserRepository userRepository;

    public List<MerchantScoreDto> getMerchantFeedback(UUID merchantId) {
        validateAdminAccess();
        return learningAnalyzer.analyzeMerchantFeedback(merchantId);
    }

    private void validateAdminAccess() {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Authenticated user not found", HttpStatus.UNAUTHORIZED));
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.SUPER_ADMIN) {
            throw new BusinessException("Access denied", HttpStatus.FORBIDDEN);
        }
    }
}
