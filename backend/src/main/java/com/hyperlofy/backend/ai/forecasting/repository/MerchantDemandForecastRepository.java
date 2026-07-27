package com.hyperlofy.backend.ai.forecasting.repository;

import com.hyperlofy.backend.ai.forecasting.entity.MerchantDemandForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MerchantDemandForecastRepository extends JpaRepository<MerchantDemandForecast, UUID> {
    List<MerchantDemandForecast> findByMerchantIdOrderByCreatedAtDesc(UUID merchantId);
    List<MerchantDemandForecast> findByForecastType(String forecastType);
}
