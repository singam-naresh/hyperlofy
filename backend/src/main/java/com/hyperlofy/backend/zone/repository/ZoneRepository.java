package com.hyperlofy.backend.zone.repository;

import com.hyperlofy.backend.zone.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, UUID> {
    Optional<Zone> findByNameIgnoreCase(String name);
    List<Zone> findByActiveTrue();
}
