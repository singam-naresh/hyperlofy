package com.hyperlofy.backend.security.repository;

import com.hyperlofy.backend.security.entity.IdentityLifecycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IdentityLifecycleRepository extends JpaRepository<IdentityLifecycle, UUID> {
    List<IdentityLifecycle> findByUserId(UUID userId);
}
