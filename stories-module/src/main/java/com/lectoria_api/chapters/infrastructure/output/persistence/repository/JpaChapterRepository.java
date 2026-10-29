package com.lectoria_api.chapters.infrastructure.output.persistence.repository;

import com.lectoria_api.chapters.infrastructure.output.persistence.entities.ChapterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaChapterRepository extends JpaRepository<ChapterEntity, UUID> {
    Page<ChapterEntity> findByStoryIdOrderByOrderAsc(UUID storyId, Pageable pageable);
    Page<ChapterEntity> findByStoryIdAndHiddenFalseOrderByOrderAsc(UUID storyId, Pageable pageable);
    Optional<ChapterEntity> findByIdAndStoryId(UUID id, UUID storyId);
    Optional<ChapterEntity> findFirstByStoryIdOrderByLastActivityAtDescCreatedAtDesc(UUID storyId);
    long countByStoryId(UUID storyId);
    Optional<ChapterEntity> findFirstByStoryIdOrderByOrderDesc(UUID storyId);
    boolean existsByStoryIdAndOrder(UUID storyId, Integer order);
    boolean existsByStoryIdAndOrderAndIdNot(UUID storyId, Integer order, UUID id);
}
