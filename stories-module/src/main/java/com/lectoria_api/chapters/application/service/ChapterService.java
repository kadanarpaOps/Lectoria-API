package com.lectoria_api.chapters.application.service;

import com.lectoria_api.chapters.domain.exceptions.ChapterNotFoundException;
import com.lectoria_api.chapters.domain.exceptions.ChapterOwnershipException;
import com.lectoria_api.chapters.domain.exceptions.InvalidChapterDataException;
import com.lectoria_api.chapters.domain.model.ChapterFilters;
import com.lectoria_api.chapters.domain.model.ChapterModel;
import com.lectoria_api.chapters.domain.ports.input.ChapterUseCases;
import com.lectoria_api.chapters.domain.ports.output.ChapterRepositoryPort;
import com.lectoria_api.comments.domain.ports.output.CommentRepositoryPort;
import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.common.domain.model.PaginationResult;
import com.lectoria_api.stories.domain.ports.input.StoryUseCases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChapterService implements ChapterUseCases {
    private static final int MAX_PAGE_SIZE = 100;
    private final ChapterRepositoryPort chapters;
    private final CommentRepositoryPort comments;
    private final StoryUseCases stories;

    @Override
    @Transactional
    public ChapterModel createChapter(UUID storyId) {
        if (storyId == null) throw new InvalidChapterDataException("storyId is required");
        ensureOwnedStory(storyId);
        int order = chapters.maxOrderByStoryId(storyId) + 1;
        ChapterModel chapter = ChapterModel.builder().id(UUID.randomUUID()).storyId(storyId).order(order)
                .title("Chapter " + order).content("").hidden(true).totalComments(0).build();
        ChapterModel saved = chapters.save(chapter);
        stories.updateChapterCount(storyId, Math.toIntExact(chapters.countByStoryId(storyId)));
        return saved;
    }

    @Override
    public ChapterModel chapter(UUID id) {
        ChapterModel chapter = chapters.findById(id).orElseThrow(ChapterNotFoundException::new);
        stories.story(chapter.getStoryId());
        if (Boolean.TRUE.equals(chapter.getHidden()) && !stories.isOwnedStory(chapter.getStoryId())) {
            throw new ChapterNotFoundException();
        }
        return chapter;
    }

    @Override
    public PageResponse<ChapterModel> chapters(ChapterFilters filters, boolean owned) {
        if (filters == null || filters.getStoryId() == null) {
            throw new InvalidChapterDataException("storyId is required");
        }
        stories.story(filters.getStoryId());
        if (owned) ensureOwnedStory(filters.getStoryId());
        filters.setVisibleOnly(!owned);
        PaginationResult<ChapterModel> result = chapters.chapters(filters,
                pagination(filters.getPageNumber(), filters.getPageSize(), filters.getOffset(), filters.getLimit()));
        return response(result);
    }

    @Override
    @Transactional
    public ChapterModel updateChapter(UUID id, ChapterModel input) {
        if (input == null) throw new InvalidChapterDataException("Chapter data is required");
        ChapterModel chapter = chapter(id);
        ensureOwnedStory(chapter.getStoryId());
        if (input.getTitle() != null) {
            if (input.getTitle().isBlank()) throw new InvalidChapterDataException("Title cannot be blank");
            chapter.setTitle(input.getTitle().trim());
        }
        if (input.getContent() != null) chapter.setContent(input.getContent());
        if (input.getOrder() != null) {
            if (input.getOrder() < 1) throw new InvalidChapterDataException("Chapter order must be positive");
            if (chapters.existsByStoryIdAndOrderAndIdNot(chapter.getStoryId(), input.getOrder(), id)) {
                throw new InvalidChapterDataException("Chapter order already exists");
            }
            chapter.setOrder(input.getOrder());
        }
        return chapters.save(chapter);
    }

    @Override
    @Transactional
    public ChapterModel toggleChapter(UUID id) {
        ChapterModel chapter = chapter(id);
        ensureOwnedStory(chapter.getStoryId());
        chapter.setHidden(!Boolean.TRUE.equals(chapter.getHidden()));
        return chapters.save(chapter);
    }

    @Override
    @Transactional
    public void deleteChapter(UUID storyId, UUID chapterId) {
        ensureOwnedStory(storyId);
        ChapterModel chapter = chapters.findByIdAndStoryId(chapterId, storyId)
                .orElseThrow(ChapterNotFoundException::new);
        chapters.delete(chapter);
        stories.updateChapterCount(storyId, Math.toIntExact(chapters.countByStoryId(storyId)));
        stories.updateCommentCount(storyId, Math.toIntExact(comments.countByStoryId(storyId)));
    }

    @Override
    @Transactional
    public void updateCommentCount(UUID chapterId, int count) {
        ChapterModel chapter = chapter(chapterId);
        chapter.setTotalComments(count);
        chapters.save(chapter);
    }

    private PaginationRequest pagination(Integer pageNumber, Integer pageSize, Integer offset, Integer limit) {
        int size = limit == null ? (pageSize == null ? 20 : pageSize) : limit;
        if (size < 1 || (offset != null && offset < 0)) throw new InvalidChapterDataException("Invalid pagination values");
        int page = offset == null ? (pageNumber == null ? 0 : pageNumber) : offset / size;
        if (page < 0) throw new InvalidChapterDataException("Invalid pagination values");
        return PaginationRequest.builder().pageNumber(page).pageSize(Math.min(size, MAX_PAGE_SIZE)).build();
    }

    private void ensureOwnedStory(UUID storyId) {
        if (!stories.isOwnedStory(storyId)) throw new ChapterOwnershipException();
    }

    private PageResponse<ChapterModel> response(PaginationResult<ChapterModel> result) {
        return PageResponse.<ChapterModel>builder().data(result.getContent()).metaData(result.toMetaData()).build();
    }
}
