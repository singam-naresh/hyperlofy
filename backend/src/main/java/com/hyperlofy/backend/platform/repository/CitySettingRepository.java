package com.hyperlofy.backend.platform.repository;

import com.hyperlofy.backend.platform.entity.CitySetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CitySettingRepository extends JpaRepository<CitySetting, UUID> {
    Optional<CitySetting> findByCityName(String cityName);
}
