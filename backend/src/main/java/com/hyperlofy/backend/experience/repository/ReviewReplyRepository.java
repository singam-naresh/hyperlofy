package com.hyperlofy.backend.experience.repository;

import com.hyperlofy.backend.experience.entity.ReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewReplyRepository extends JpaRepository<ReviewReply, UUID> {
    List<ReviewReply> findByReview_Id(UUID reviewId);
}
