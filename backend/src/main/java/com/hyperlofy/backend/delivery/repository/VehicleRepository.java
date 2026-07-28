package com.hyperlofy.backend.delivery.repository;

import com.hyperlofy.backend.delivery.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    Optional<Vehicle> findByDeliveryPartnerId(UUID deliveryPartnerId);
    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);
}
