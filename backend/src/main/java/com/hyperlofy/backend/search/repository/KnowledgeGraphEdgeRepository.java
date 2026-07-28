package com.hyperlofy.backend.search.repository;

import com.hyperlofy.backend.search.entity.KnowledgeGraphEdge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KnowledgeGraphEdgeRepository extends JpaRepository<KnowledgeGraphEdge, UUID> {
    List<KnowledgeGraphEdge> findBySourceNode_Id(UUID sourceNodeId);
    List<KnowledgeGraphEdge> findByTargetNode_Id(UUID targetNodeId);
}
