package com.hyperlofy.backend.user.service;

import com.hyperlofy.backend.agent.entity.AgentProfile;
import com.hyperlofy.backend.agent.entity.VerificationStatus;
import com.hyperlofy.backend.agent.repository.AgentRepository;
import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.customer.entity.CustomerProfile;
import com.hyperlofy.backend.customer.repository.CustomerRepository;
import com.hyperlofy.backend.security.jwt.JwtTokenProvider;
import com.hyperlofy.backend.user.dto.AuthDto;
import com.hyperlofy.backend.user.entity.RefreshToken;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.RefreshTokenRepository;
import com.hyperlofy.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AgentRepository agentRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthDto.TokenResponse register(AuthDto.RegisterRequest request) {
        log.info("Initiating onboarding registration workflow for: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("An account is already registered with this email.", HttpStatus.CONFLICT);
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BusinessException("An account is already registered with this phone number.", HttpStatus.CONFLICT);
        }

        // Build base user identity
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .active(true)
                .build();
        
        user = userRepository.save(user);

        // Branch according to target roles
        switch (request.getRole()) {
            case CUSTOMER:
                CustomerProfile customerProfile = CustomerProfile.builder()
                        .user(user)
                        .defaultDeliveryAddress(request.getDefaultDeliveryAddress())
                        .gpsLatitude(request.getGpsLatitude())
                        .gpsLongitude(request.getGpsLongitude())
                        .preferredPaymentMethod("CARD")
                        .build();
                customerRepository.save(customerProfile);
                break;

            case AGENT:
                // Constraints validation
                if (request.getVehicleType() == null || request.getVehicleNumber() == null) {
                    throw new BusinessException("Agents must register vehicle specifications.", HttpStatus.BAD_REQUEST);
                }
                if (request.getPanNumber() == null || request.getAadhaarNumber() == null) {
                    throw new BusinessException("Agents must register PAN and Aadhaar identity keys.", HttpStatus.BAD_REQUEST);
                }
                if (agentRepository.existsByPanNumber(request.getPanNumber())) {
                    throw new BusinessException("Provided PAN number is already registered in our system.", HttpStatus.CONFLICT);
                }
                if (agentRepository.existsByAadhaarNumber(request.getAadhaarNumber())) {
                    throw new BusinessException("Provided Aadhaar number is already registered in our system.", HttpStatus.CONFLICT);
                }

                AgentProfile agentProfile = AgentProfile.builder()
                        .user(user)
                        .vehicleType(request.getVehicleType())
                        .vehicleNumber(request.getVehicleNumber())
                        .panNumber(request.getPanNumber())
                        .aadhaarNumber(request.getAadhaarNumber())
                        .verificationStatus(VerificationStatus.PENDING)
                        .available(false)
                        .build();
                agentRepository.save(agentProfile);
                break;

            case ADMIN:
            case SUPER_ADMIN:
                // Admin roles do not require secondary visual profile cards in Phase 1
                break;

            default:
                throw new BusinessException("Invalid profile tier requested.", HttpStatus.BAD_REQUEST);
        }

        return generateTokenPayload(user);
    }

    @Transactional
    public AuthDto.TokenResponse login(AuthDto.LoginRequest request) {
        log.info("Processing user authorization validation for: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Invalid email or password credentials.", HttpStatus.UNAUTHORIZED));

        if (!user.isActive()) {
            throw new BusinessException("Your account is currently disabled. Contact administrative support.", HttpStatus.FORBIDDEN);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException("Invalid email or password credentials.", HttpStatus.UNAUTHORIZED);
        }

        // On login, clear previous access sessions for safety and regenerate
        refreshTokenRepository.deleteByUserId(user.getId());

        return generateTokenPayload(user);
    }

    @Transactional
    public AuthDto.TokenResponse refresh(AuthDto.RefreshRequest request) {
        String token = request.getRefreshToken();
        
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Active login session not found.", HttpStatus.UNAUTHORIZED));

        if (refreshToken.isRevoked()) {
            throw new BusinessException("Session token has been blacklisted or revoked.", HttpStatus.UNAUTHORIZED);
        }

        if (refreshToken.getExpiryDate().isBefore(OffsetDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new BusinessException("Session expired. Please log in again.", HttpStatus.UNAUTHORIZED);
        }

        User user = refreshToken.getUser();
        if (!user.isActive()) {
            throw new BusinessException("User profile is currently inactive.", HttpStatus.FORBIDDEN);
        }

        // Rotate access token and update existing refresh session timestamp
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole().name(), user.getId().toString());
        refreshToken.setUpdatedAt(OffsetDateTime.now());
        refreshTokenRepository.save(refreshToken);

        return AuthDto.TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(token)
                .tokenType("Bearer")
                .userId(user.getId().toString())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Transactional
    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        String token = authHeader.substring(7);
        try {
            if (jwtTokenProvider.validateToken(token)) {
                String email = jwtTokenProvider.getEmailFromToken(token);
                userRepository.findByEmail(email).ifPresent(user -> {
                    refreshTokenRepository.deleteByUserId(user.getId());
                    log.info("Sessions revoked successfully for user: {}", email);
                });
            }
        } catch (Exception e) {
            log.error("Error executing user logout profile cleanup", e);
        }
    }

    @Transactional
    public void resetPassword(AuthDto.PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("User identity not found.", HttpStatus.NOT_FOUND));
        
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        // Evict sessions to force re-authentication
        refreshTokenRepository.deleteByUserId(user.getId());
        log.info("Password updated. Existing sessions evicted for: {}", request.getEmail());
    }

    private AuthDto.TokenResponse generateTokenPayload(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getEmail(), 
                user.getRole().name(), 
                user.getId().toString()
        );
        String rTokenValue = jwtTokenProvider.generateRefreshToken(user.getEmail());

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(rTokenValue)
                .expiryDate(OffsetDateTime.now().plusSeconds(604800)) // 7 days matching token provider specs
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return AuthDto.TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rTokenValue)
                .tokenType("Bearer")
                .userId(user.getId().toString())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
