package com.hyperlofy.backend.admin.repository;

import com.hyperlofy.backend.admin.entity.AdminNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdminNoteRepository extends JpaRepository<AdminNote, UUID> {
    List<AdminNote> findByTargetIdAndTargetTypeOrderByCreatedAtDesc(UUID targetId, String targetType);
}
