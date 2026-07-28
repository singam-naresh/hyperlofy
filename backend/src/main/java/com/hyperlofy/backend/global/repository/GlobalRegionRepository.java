package com.hyperlofy.backend.global.repository;

import com.hyperlofy.backend.global.entity.GlobalRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GlobalRegionRepository extends JpaRepository<GlobalRegion, UUID> {
    Optional<GlobalRegion> findByRegionCode(String regionCode);
}
