package com.hyperlofy.backend.eip.repository;

import com.hyperlofy.backend.eip.entity.MasterDataRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MasterDataRegistryRepository extends JpaRepository<MasterDataRegistry, UUID> {
    Optional<MasterDataRegistry> findByMasterCode(String masterCode);
}
