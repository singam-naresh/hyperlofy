package com.hyperlofy.backend.devex.repository;

import com.hyperlofy.backend.devex.entity.ApiRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApiRouteRepository extends JpaRepository<ApiRoute, UUID> {
    Optional<ApiRoute> findByRouteId(String routeId);
}
