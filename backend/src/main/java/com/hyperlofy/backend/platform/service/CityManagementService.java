package com.hyperlofy.backend.platform.service;

import com.hyperlofy.backend.platform.entity.CitySetting;
import com.hyperlofy.backend.platform.repository.CitySettingRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityManagementService {

    private static final Logger log = LoggerFactory.getLogger(CityManagementService.class);

    private final CitySettingRepository cityRepository;

    @Transactional
    @CacheEvict(value = "platform_cities", allEntries = true)
    public CitySetting configureCity(CitySetting citySetting) {
        log.info("Configuring city operating settings: city={}", citySetting.getCityName());
        return cityRepository.save(citySetting);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "platform_cities", key = "'all_active'")
    public List<CitySetting> getAllActiveCities() {
        return cityRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsActive()))
                .toList();
    }
}
