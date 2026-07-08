package com.hyperlofy.backend.customer.controller;

import com.hyperlofy.backend.customer.dto.CustomerDto;
import com.hyperlofy.backend.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/customer")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/profile")
    public ResponseEntity<CustomerDto.ProfileResponse> getProfile(Principal principal) {
        return ResponseEntity.ok(customerService.getProfileByUserEmail(principal.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<CustomerDto.ProfileResponse> updateProfile(
            Principal principal, 
            @Valid @RequestBody CustomerDto.UpdateProfileRequest request) {
        return ResponseEntity.ok(customerService.updateProfile(principal.getName(), request));
    }
}
