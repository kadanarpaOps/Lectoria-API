package com.lectoria_api.comments.infrastructure.output.persistence.repository;

import com.lectoria_api.comments.infrastructure.output.persistence.entities.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaCommentRepository extends JpaRepository<CommentEntity, UUID> {
    Page<CommentEntity> findByStoryIdOrderByCreatedAtDesc(UUID storyId, Pageable pageable);
    Page<CommentEntity> findByChapterIdOrderByCreatedAtDesc(UUID chapterId, Pageable pageable);
    long countByStoryId(UUID storyId);
    long countByChapterId(UUID chapterId);
}
