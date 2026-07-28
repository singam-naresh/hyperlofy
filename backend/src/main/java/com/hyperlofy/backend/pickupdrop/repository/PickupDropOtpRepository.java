package com.hyperlofy.backend.pickupdrop.repository;

import com.hyperlofy.backend.pickupdrop.entity.PickupDropOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PickupDropOtpRepository extends JpaRepository<PickupDropOtp, UUID> {
    Optional<PickupDropOtp> findByOrderIdAndOtpTypeAndIsVerifiedFalse(UUID orderId, String otpType);
}
