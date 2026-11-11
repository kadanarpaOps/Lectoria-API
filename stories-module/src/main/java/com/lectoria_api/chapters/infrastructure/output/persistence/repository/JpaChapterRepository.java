package com.lectoria_api.chapters.infrastructure.output.persistence.repository;

import com.lectoria_api.chapters.infrastructure.output.persistence.entities.ChapterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaChapterRepository extends JpaRepository<ChapterEntity, UUID> {
    Page<ChapterEntity> findByStoryId(UUID storyId, Pageable pageable);
    Page<ChapterEntity> findByStoryIdAndHiddenFalse(UUID storyId, Pageable pageable);
    Optional<ChapterEntity> findByIdAndStoryId(UUID id, UUID storyId);
    Optional<ChapterEntity> findFirstByStoryIdOrderByLastActivityAtDescCreatedAtDesc(UUID storyId);
    long countByStoryId(UUID storyId);
    Optional<ChapterEntity> findFirstByStoryIdOrderByOrderDesc(UUID storyId);
    java.util.List<ChapterEntity> findByStoryIdOrderByOrderAsc(UUID storyId);
    boolean existsByStoryIdAndOrder(UUID storyId, Integer order);
    boolean existsByStoryIdAndOrderAndIdNot(UUID storyId, Integer order, UUID id);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE ChapterEntity chapter SET chapter.order = -chapter.order WHERE chapter.storyId = :storyId")
    void moveOrdersToTemporaryValues(@Param("storyId") UUID storyId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE ChapterEntity chapter SET chapter.order = :order WHERE chapter.id = :chapterId")
    void updateOrder(@Param("chapterId") UUID chapterId, @Param("order") int order);
}
