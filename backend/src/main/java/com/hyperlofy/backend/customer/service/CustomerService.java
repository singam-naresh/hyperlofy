package com.hyperlofy.backend.customer.service;

import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.customer.dto.CustomerDto;
import com.hyperlofy.backend.customer.entity.CustomerProfile;
import com.hyperlofy.backend.customer.repository.CustomerRepository;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public CustomerDto.ProfileResponse getProfileByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User profile not found.", HttpStatus.NOT_FOUND));

        CustomerProfile profile = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException("Customer profile record not found.", HttpStatus.NOT_FOUND));

        return mapToResponse(profile, user);
    }

    @Transactional
    public CustomerDto.ProfileResponse updateProfile(String email, CustomerDto.UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User profile not found.", HttpStatus.NOT_FOUND));

        CustomerProfile profile = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException("Customer profile record not found.", HttpStatus.NOT_FOUND));

        if (request.getDefaultDeliveryAddress() != null) {
            profile.setDefaultDeliveryAddress(request.getDefaultDeliveryAddress());
        }
        if (request.getGpsLatitude() != null) {
            profile.setGpsLatitude(request.getGpsLatitude());
        }
        if (request.getGpsLongitude() != null) {
            profile.setGpsLongitude(request.getGpsLongitude());
        }
        if (request.getPreferredPaymentMethod() != null) {
            profile.setPreferredPaymentMethod(request.getPreferredPaymentMethod());
        }

        profile.setUpdatedBy(email);
        profile = customerRepository.save(profile);

        return mapToResponse(profile, user);
    }

    private CustomerDto.ProfileResponse mapToResponse(CustomerProfile profile, User user) {
        return CustomerDto.ProfileResponse.builder()
                .profileId(profile.getId())
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .defaultDeliveryAddress(profile.getDefaultDeliveryAddress())
                .gpsLatitude(profile.getGpsLatitude())
                .gpsLongitude(profile.getGpsLongitude())
                .preferredPaymentMethod(profile.getPreferredPaymentMethod())
                .build();
    }
}
