package com.hyperlofy.backend.agent.service;

import com.hyperlofy.backend.agent.dto.AgentDto;
import com.hyperlofy.backend.agent.entity.AgentProfile;
import com.hyperlofy.backend.agent.entity.VerificationStatus;
import com.hyperlofy.backend.agent.repository.AgentRepository;
import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentService {

    private final AgentRepository agentRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public AgentDto.ProfileResponse getAgentProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User profile not found.", HttpStatus.NOT_FOUND));

        AgentProfile profile = agentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException("Agent profile record not found.", HttpStatus.NOT_FOUND));

        return mapToResponse(profile, user);
    }

    @Transactional
    public AgentDto.ProfileResponse updateLocation(String email, AgentDto.LocationUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User profile not found.", HttpStatus.NOT_FOUND));

        AgentProfile profile = agentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException("Agent profile record not found.", HttpStatus.NOT_FOUND));

        // Let verified agents update location
        profile.setCurrentGpsLatitude(request.getLatitude());
        profile.setCurrentGpsLongitude(request.getLongitude());
        profile.setUpdatedBy(email);
        
        agentRepository.save(profile);
        log.debug("Agent location updated: lat={}, lon={} for agent email={}", request.getLatitude(), request.getLongitude(), email);

        return mapToResponse(profile, user);
    }

    @Transactional
    public AgentDto.ProfileResponse updateAvailability(String email, boolean available) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User profile not found.", HttpStatus.NOT_FOUND));

        AgentProfile profile = agentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException("Agent profile record not found.", HttpStatus.NOT_FOUND));

        // Strictly enforce security rule: Agents can change availability ONLY if verification is APPROVED!
        if (available && profile.getVerificationStatus() != VerificationStatus.APPROVED) {
            throw new BusinessException(
                    "You cannot change availability to active because your verification status is: " + profile.getVerificationStatus() + ".",
                    HttpStatus.FORBIDDEN
            );
        }

        profile.setAvailable(available);
        profile.setUpdatedBy(email);
        agentRepository.save(profile);

        log.info("Agent availability status changed to {} for agent email={}", available, email);
        return mapToResponse(profile, user);
    }

    @Transactional
    public AgentDto.ProfileResponse uploadDocuments(String email, AgentDto.UploadDocumentsRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User profile not found.", HttpStatus.NOT_FOUND));

        AgentProfile profile = agentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException("Agent profile record not found.", HttpStatus.NOT_FOUND));

        // If documents are re-uploaded, transition back to PENDING verification status
        profile.setPanDocUrl(request.getPanDocUrl());
        profile.setAadhaarDocUrl(request.getAadhaarDocUrl());
        profile.setProfileImageUrl(request.getProfileImageUrl());
        profile.setVerificationStatus(VerificationStatus.PENDING);
        profile.setRejectionReason(null);
        profile.setUpdatedBy(email);

        agentRepository.save(profile);
        log.info("Agent documents uploaded successfully. Profile reset to PENDING verification workflow for: {}", email);

        return mapToResponse(profile, user);
    }

    private AgentDto.ProfileResponse mapToResponse(AgentProfile profile, User user) {
        return AgentDto.ProfileResponse.builder()
                .profileId(profile.getId())
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .vehicleType(profile.getVehicleType())
                .vehicleNumber(profile.getVehicleNumber())
                .currentGpsLatitude(profile.getCurrentGpsLatitude())
                .currentGpsLongitude(profile.getCurrentGpsLongitude())
                .isAvailable(profile.isAvailable())
                .panNumber(profile.getPanNumber())
                .panDocUrl(profile.getPanDocUrl())
                .aadhaarNumber(profile.getAadhaarNumber())
                .aadhaarDocUrl(profile.getAadhaarDocUrl())
                .profileImageUrl(profile.getProfileImageUrl())
                .verificationStatus(profile.getVerificationStatus())
                .rejectionReason(profile.getRejectionReason())
                .suspendedAt(profile.getSuspendedAt())
                .suspensionReason(profile.getSuspensionReason())
                .build();
    }
}
