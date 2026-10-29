package com.lectoria_api.chapters.domain.ports.input;

import com.lectoria_api.chapters.domain.model.ChapterFilters;
import com.lectoria_api.chapters.domain.model.ChapterModel;
import com.lectoria_api.common.domain.model.PageResponse;

import java.util.UUID;

public interface ChapterUseCases {
    ChapterModel createChapter(UUID storyId);
    ChapterModel chapter(UUID id);
    PageResponse<ChapterModel> chapters(ChapterFilters filters, boolean owned);
    ChapterModel updateChapter(UUID id, ChapterModel input);
    ChapterModel toggleChapter(UUID id);
    void deleteChapter(UUID storyId, UUID chapterId);
    void updateCommentCount(UUID chapterId, int count);
}
