package com.lectoria_api.chapters.domain.ports.output;

import com.lectoria_api.chapters.domain.model.ChapterFilters;
import com.lectoria_api.chapters.domain.model.ChapterModel;
import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.common.domain.model.PaginationResult;

import java.util.Optional;
import java.util.UUID;

public interface ChapterRepositoryPort {
    ChapterModel save(ChapterModel chapter);
    Optional<ChapterModel> findById(UUID id);
    Optional<ChapterModel> findByIdAndStoryId(UUID chapterId, UUID storyId);
    PaginationResult<ChapterModel> chapters(ChapterFilters filters, PaginationRequest request);
    boolean existsByStoryIdAndOrder(UUID storyId, Integer order);
    boolean existsByStoryIdAndOrderAndIdNot(UUID storyId, Integer order, UUID chapterId);
    long countByStoryId(UUID storyId);
    int maxOrderByStoryId(UUID storyId);
    void delete(ChapterModel chapter);
}
