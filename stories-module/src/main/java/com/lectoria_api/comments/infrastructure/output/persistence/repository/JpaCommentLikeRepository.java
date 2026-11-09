package com.lectoria_api.comments.infrastructure.output.persistence.repository;

import com.lectoria_api.comments.infrastructure.output.persistence.entities.CommentLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaCommentLikeRepository extends JpaRepository<CommentLikeEntity, UUID> {
    Optional<CommentLikeEntity> findByCommentIdAndUserId(UUID commentId, UUID userId);
    long countByCommentId(UUID commentId);
}
