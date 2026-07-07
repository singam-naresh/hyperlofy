package com.hyperlofy.backend.admin.service;

import com.hyperlofy.backend.admin.dto.AdminDto;
import com.hyperlofy.backend.agent.dto.AgentDto;
import com.hyperlofy.backend.agent.entity.AgentProfile;
import com.hyperlofy.backend.agent.entity.AgentVerificationLog;
import com.hyperlofy.backend.agent.entity.VerificationStatus;
import com.hyperlofy.backend.agent.repository.AgentRepository;
import com.hyperlofy.backend.agent.repository.AgentVerificationLogRepository;
import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.customer.repository.CustomerRepository;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final AgentRepository agentRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AgentVerificationLogRepository logRepository;

    @Transactional(readOnly = true)
    public List<AgentDto.ProfileResponse> getAgentsByStatus(VerificationStatus status) {
        return agentRepository.findByVerificationStatus(status).stream()
                .map(agent -> mapToResponse(agent, agent.getUser()))
                .collect(Collectors.toList());
    }

    @Transactional
    public AgentDto.ProfileResponse approveAgent(String adminEmail, UUID agentProfileId, AdminDto.ApproveAgentRequest request) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new BusinessException("Admin user identity not found.", HttpStatus.NOT_FOUND));

        AgentProfile agent = agentRepository.findById(agentProfileId)
                .orElseThrow(() -> new BusinessException("Agent profile not found.", HttpStatus.NOT_FOUND));

        VerificationStatus previousStatus = agent.getVerificationStatus();
        if (previousStatus == VerificationStatus.APPROVED) {
            throw new BusinessException("Agent is already approved.", HttpStatus.BAD_REQUEST);
        }

        // Transition status
        agent.setVerificationStatus(VerificationStatus.APPROVED);
        agent.setRejectionReason(null);
        agent.setSuspensionReason(null);
        agent.setSuspendedAt(null);
        agent.setUpdatedBy(adminEmail);
        agent = agentRepository.save(agent);

        // Record verification audit
        AgentVerificationLog auditLog = AgentVerificationLog.builder()
                .agent(agent)
                .admin(admin)
                .previousStatus(previousStatus)
                .newStatus(VerificationStatus.APPROVED)
                .remarks(request.getRemarks())
                .createdAt(OffsetDateTime.now())
                .build();
        logRepository.save(auditLog);

        log.info("Agent APPROVED successfully by Admin: {}. Agent ID: {}", adminEmail, agent.getId());
        return mapToResponse(agent, agent.getUser());
    }

    @Transactional
    public AgentDto.ProfileResponse rejectAgent(String adminEmail, UUID agentProfileId, AdminDto.RejectAgentRequest request) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new BusinessException("Admin user identity not found.", HttpStatus.NOT_FOUND));

        AgentProfile agent = agentRepository.findById(agentProfileId)
                .orElseThrow(() -> new BusinessException("Agent profile not found.", HttpStatus.NOT_FOUND));

        VerificationStatus previousStatus = agent.getVerificationStatus();
        if (previousStatus != VerificationStatus.PENDING) {
            throw new BusinessException("Only pending agents can be rejected.", HttpStatus.BAD_REQUEST);
        }

        // Transition status
        agent.setVerificationStatus(VerificationStatus.REJECTED);
        agent.setRejectionReason(request.getRejectionReason());
        agent.setUpdatedBy(adminEmail);
        agent = agentRepository.save(agent);

        // Record verification audit
        AgentVerificationLog auditLog = AgentVerificationLog.builder()
                .agent(agent)
                .admin(admin)
                .previousStatus(previousStatus)
                .newStatus(VerificationStatus.REJECTED)
                .remarks("Rejection: " + request.getRejectionReason())
                .createdAt(OffsetDateTime.now())
                .build();
        logRepository.save(auditLog);

        log.warn("Agent REJECTED by Admin: {}. Agent ID: {}, Reason: {}", adminEmail, agent.getId(), request.getRejectionReason());
        return mapToResponse(agent, agent.getUser());
    }

    @Transactional
    public AgentDto.ProfileResponse suspendAgent(String adminEmail, UUID agentProfileId, AdminDto.SuspendAgentRequest request) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new BusinessException("Admin user identity not found.", HttpStatus.NOT_FOUND));

        AgentProfile agent = agentRepository.findById(agentProfileId)
                .orElseThrow(() -> new BusinessException("Agent profile not found.", HttpStatus.NOT_FOUND));

        VerificationStatus previousStatus = agent.getVerificationStatus();
        if (previousStatus == VerificationStatus.SUSPENDED) {
            throw new BusinessException("Agent profile is already suspended.", HttpStatus.BAD_REQUEST);
        }

        // Transition status
        agent.setVerificationStatus(VerificationStatus.SUSPENDED);
        agent.setSuspendedAt(OffsetDateTime.now());
        agent.setSuspensionReason(request.getSuspensionReason());
        agent.setAvailable(false); // Force remove from active delivery availability pool!
        agent.setUpdatedBy(adminEmail);
        agent = agentRepository.save(agent);

        // Record verification audit
        AgentVerificationLog auditLog = AgentVerificationLog.builder()
                .agent(agent)
                .admin(admin)
                .previousStatus(previousStatus)
                .newStatus(VerificationStatus.SUSPENDED)
                .remarks("Administrative Suspension: " + request.getSuspensionReason())
                .createdAt(OffsetDateTime.now())
                .build();
        logRepository.save(auditLog);

        log.warn("Agent SUSPENDED administratively by: {}. Agent ID: {}, Reason: {}", adminEmail, agent.getId(), request.getSuspensionReason());
        return mapToResponse(agent, agent.getUser());
    }

    @Transactional(readOnly = true)
    public List<AdminDto.VerificationLogResponse> getAgentVerificationLogs(UUID agentProfileId) {
        return logRepository.findByAgentId(agentProfileId).stream()
                .map(this::mapToAuditLogResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AdminDto.SystemStatsResponse getSystemStats() {
        return AdminDto.SystemStatsResponse.builder()
                .totalUsersCount(userRepository.count())
                .customerCount(customerRepository.count())
                .agentCount(agentRepository.count())
                .pendingVerificationCount(agentRepository.findByVerificationStatus(VerificationStatus.PENDING).size())
                .approvedVerificationCount(agentRepository.findByVerificationStatus(VerificationStatus.APPROVED).size())
                .rejectedVerificationCount(agentRepository.findByVerificationStatus(VerificationStatus.REJECTED).size())
                .suspendedVerificationCount(agentRepository.findByVerificationStatus(VerificationStatus.SUSPENDED).size())
                .build();
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

    private AdminDto.VerificationLogResponse mapToAuditLogResponse(AgentVerificationLog log) {
        return AdminDto.VerificationLogResponse.builder()
                .logId(log.getId())
                .agentId(log.getAgent().getId())
                .adminEmail(log.getAdmin().getEmail())
                .previousStatus(log.getPreviousStatus())
                .newStatus(log.getNewStatus())
                .remarks(log.getRemarks())
                .timestamp(log.getCreatedAt())
                .build();
    }
}
