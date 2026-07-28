package com.hyperlofy.backend.buyforme.service;

import com.hyperlofy.backend.buyforme.entity.BuyForMeOrder;
import com.hyperlofy.backend.buyforme.entity.BuyForMePriceBreakdown;
import com.hyperlofy.backend.buyforme.entity.BuyForMePurchaseProof;
import com.hyperlofy.backend.buyforme.repository.BuyForMeOrderRepository;
import com.hyperlofy.backend.buyforme.repository.BuyForMePriceBreakdownRepository;
import com.hyperlofy.backend.buyforme.repository.BuyForMePurchaseProofRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuyForMeService {

    private static final Logger log = LoggerFactory.getLogger(BuyForMeService.class);

    private final BuyForMeOrderRepository orderRepository;
    private final BuyForMePurchaseProofRepository proofRepository;
    private final BuyForMePriceBreakdownRepository priceRepository;

    @Transactional
    public BuyForMeOrder createCustomerRequest(UUID customerId, String title, String category, Double maxBudget, String address, Double lat, Double lng) {
        String num = "BFM-" + System.currentTimeMillis();
        log.info("[BUY FOR ME] Creating request CustomerId={}, OrderNum={}, Title={}, Category={}, Budget={}", customerId, num, title, category, maxBudget);

        BuyForMeOrder order = BuyForMeOrder.builder()
                .orderNumber(num)
                .customerId(customerId)
                .title(title)
                .category(category)
                .maxBudget(maxBudget)
                .deliveryAddress(address)
                .deliveryLatitude(lat)
                .deliveryLongitude(lng)
                .status("REQUESTED")
                .build();

        BuyForMeOrder saved = orderRepository.save(order);

        // Pre-calculate initial price breakdown estimate
        double delFee = 40.0;
        double servFee = 15.0;
        double total = maxBudget + delFee + servFee;

        priceRepository.save(BuyForMePriceBreakdown.builder()
                .orderId(saved.getId())
                .productCost(maxBudget)
                .deliveryFee(delFee)
                .serviceFee(servFee)
                .totalPayable(total)
                .build());

        return saved;
    }

    @Transactional
    public BuyForMeOrder assignDriver(UUID orderId, UUID driverId) {
        BuyForMeOrder order = getOrderById(orderId);
        log.info("[BUY FOR ME] Driver Assigned OrderId={}, DriverId={}", orderId, driverId);
        order.setAssignedDriverId(driverId);
        order.setStatus("DRIVER_ASSIGNED");
        return orderRepository.save(order);
    }

    @Transactional
    public BuyForMePurchaseProof uploadPurchaseProof(UUID orderId, UUID driverId, String storeName, Double billAmount) {
        BuyForMeOrder order = getOrderById(orderId);
        log.info("[BUY FOR ME] Purchase Proof Uploaded OrderId={}, Store={}, Bill={}", orderId, storeName, billAmount);
        
        order.setStatus("WAITING_CUSTOMER_APPROVAL");
        orderRepository.save(order);

        BuyForMePurchaseProof proof = BuyForMePurchaseProof.builder()
                .orderId(orderId)
                .driverId(driverId)
                .storeName(storeName)
                .billAmount(billAmount)
                .isApprovedByCustomer(false)
                .build();

        return proofRepository.save(proof);
    }

    @Transactional
    public BuyForMeOrder approvePurchase(UUID orderId) {
        BuyForMeOrder order = getOrderById(orderId);
        log.info("[BUY FOR ME] Customer Approved Purchase OrderId={}", orderId);
        order.setStatus("PURCHASED");
        return orderRepository.save(order);
    }

    @Transactional
    public BuyForMeOrder updateStatus(UUID orderId, String newStatus) {
        BuyForMeOrder order = getOrderById(orderId);
        log.info("[BUY FOR ME] Status transition OrderId={}, From={}, To={}", orderId, order.getStatus(), newStatus);
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public BuyForMeOrder getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("BuyForMeOrder not found with id: " + orderId));
    }

    @Transactional(readOnly = true)
    public List<BuyForMeOrder> getCustomerOrders(UUID customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }
}
