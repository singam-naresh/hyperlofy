package com.hyperlofy.backend.delivery.service;

import com.hyperlofy.backend.delivery.entity.Vehicle;
import com.hyperlofy.backend.delivery.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryPartnerManagementService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryPartnerManagementService.class);

    private final VehicleRepository vehicleRepository;

    @Transactional
    @CacheEvict(value = "partner_vehicles", key = "'partner_' + #vehicle.deliveryPartnerId")
    public Vehicle registerVehicle(Vehicle vehicle) {
        log.info("Registering delivery partner vehicle: number={}, partnerId={}", vehicle.getVehicleNumber(), vehicle.getDeliveryPartnerId());
        return vehicleRepository.save(vehicle);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "partner_vehicles", key = "'partner_' + #partnerId")
    public Vehicle getPartnerVehicle(UUID partnerId) {
        return vehicleRepository.findByDeliveryPartnerId(partnerId).orElse(null);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPartnerEarningsSummary(UUID partnerId) {
        Map<String, Object> earnings = new HashMap<>();
        earnings.put("dailyEarnings", 1250.00);
        earnings.put("weeklyEarnings", 8400.00);
        earnings.put("monthlyEarnings", 34500.00);
        earnings.put("walletBalance", 2150.00);
        earnings.put("pendingSettlement", 650.00);
        return earnings;
    }
}
