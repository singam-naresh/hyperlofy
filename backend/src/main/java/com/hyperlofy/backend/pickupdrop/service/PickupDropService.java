package com.hyperlofy.backend.pickupdrop.service;

import com.hyperlofy.backend.pickupdrop.entity.PickupDropOrder;
import com.hyperlofy.backend.pickupdrop.entity.PickupDropOtp;

import com.hyperlofy.backend.pickupdrop.repository.PickupDropOrderRepository;
import com.hyperlofy.backend.pickupdrop.repository.PickupDropOtpRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PickupDropService {

    private static final Logger log = LoggerFactory.getLogger(PickupDropService.class);

    private final PickupDropOrderRepository orderRepository;
    private final PickupDropOtpRepository otpRepository;

    @Transactional
    public PickupDropOrder createDeliveryOrder(UUID customerId, String senderName, String senderContact, String pickupAddr, Double pLat, Double pLng,
                                                String recipientName, String recipientContact, String delAddr, Double dLat, Double dLng, String delType) {
        String num = "PD-" + System.currentTimeMillis();
        log.info("[PICKUP & DROP] Creating order CustomerId={}, Num={}, Sender={}, Recipient={}, Type={}", customerId, num, senderName, recipientName, delType);

        PickupDropOrder order = PickupDropOrder.builder()
                .orderNumber(num)
                .customerId(customerId)
                .senderName(senderName)
                .senderContact(senderContact)
                .pickupAddress(pickupAddr)
                .pickupLatitude(pLat)
                .pickupLongitude(pLng)
                .recipientName(recipientName)
                .recipientContact(recipientContact)
                .deliveryAddress(delAddr)
                .deliveryLatitude(dLat)
                .deliveryLongitude(dLng)
                .deliveryType(delType)
                .status("REQUESTED")
                .build();

        PickupDropOrder saved = orderRepository.save(order);

        // Pre-generate 6-digit Pickup and Delivery OTPs
        generateOtp(saved.getId(), "PICKUP");
        generateOtp(saved.getId(), "DELIVERY");

        return saved;
    }

    @Transactional
    public PickupDropOtp generateOtp(UUID orderId, String otpType) {
        String code = String.valueOf((int) (Math.random() * 900000) + 100000);
        log.info("[PICKUP & DROP] OTP Generated OrderId={}, Type={}, Code={}", orderId, otpType, code);

        PickupDropOtp otp = PickupDropOtp.builder()
                .orderId(orderId)
                .otpCode(code)
                .otpType(otpType)
                .isVerified(false)
                .expiresAt(ZonedDateTime.now().plusHours(2))
                .build();

        return otpRepository.save(otp);
    }

    @Transactional
    public boolean verifyOtp(UUID orderId, String otpType, String code) {
        log.info("[PICKUP & DROP] Verifying OTP OrderId={}, Type={}, Code={}", orderId, otpType, code);
        return otpRepository.findByOrderIdAndOtpTypeAndIsVerifiedFalse(orderId, otpType)
                .map(otp -> {
                    if (otp.getOtpCode().equals(code)) {
                        otp.setIsVerified(true);
                        otpRepository.save(otp);
                        
                        PickupDropOrder order = getOrderById(orderId);
                        if ("PICKUP".equals(otpType)) {
                            order.setStatus("PICKED_UP");
                        } else if ("DELIVERY".equals(otpType)) {
                            order.setStatus("DELIVERED");
                        }
                        orderRepository.save(order);
                        return true;
                    }
                    return false;
                }).orElse(false);
    }

    @Transactional
    public PickupDropOrder assignDriver(UUID orderId, UUID driverId) {
        PickupDropOrder order = getOrderById(orderId);
        log.info("[PICKUP & DROP] Driver Assigned OrderId={}, DriverId={}", orderId, driverId);
        order.setAssignedDriverId(driverId);
        order.setStatus("DRIVER_ASSIGNED");
        return orderRepository.save(order);
    }

    @Transactional
    public PickupDropOrder updateStatus(UUID orderId, String newStatus) {
        PickupDropOrder order = getOrderById(orderId);
        log.info("[PICKUP & DROP] Status transition OrderId={}, From={}, To={}", orderId, order.getStatus(), newStatus);
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public PickupDropOrder getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("PickupDropOrder not found with id: " + orderId));
    }

    @Transactional(readOnly = true)
    public List<PickupDropOrder> getCustomerOrders(UUID customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }
}
