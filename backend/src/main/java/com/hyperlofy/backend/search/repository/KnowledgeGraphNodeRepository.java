package com.hyperlofy.backend.search.repository;

import com.hyperlofy.backend.search.entity.KnowledgeGraphNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface KnowledgeGraphNodeRepository extends JpaRepository<KnowledgeGraphNode, UUID> {
    Optional<KnowledgeGraphNode> findByEntityId(String entityId);
}
