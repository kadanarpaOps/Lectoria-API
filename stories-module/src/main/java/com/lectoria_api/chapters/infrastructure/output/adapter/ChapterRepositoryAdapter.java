package com.lectoria_api.chapters.infrastructure.output.adapter;

import com.lectoria_api.chapters.domain.model.ChapterFilters;
import com.lectoria_api.chapters.domain.model.ChapterModel;
import com.lectoria_api.chapters.domain.ports.output.ChapterRepositoryPort;
import com.lectoria_api.chapters.infrastructure.output.persistence.entities.ChapterEntity;
import com.lectoria_api.chapters.infrastructure.output.persistence.mapper.ChapterPersistenceMapper;
import com.lectoria_api.chapters.infrastructure.output.persistence.repository.JpaChapterRepository;
import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.common.domain.model.PaginationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChapterRepositoryAdapter implements ChapterRepositoryPort {
    private final JpaChapterRepository chapters;
    private final ChapterPersistenceMapper mapper;

    @Override
    @Transactional
    public ChapterModel save(ChapterModel chapter) {
        return mapper.toModel(chapters.save(mapper.toEntity(chapter)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChapterModel> findById(UUID id) {
        return chapters.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChapterModel> findByIdAndStoryId(UUID chapterId, UUID storyId) {
        return chapters.findByIdAndStoryId(chapterId, storyId).map(mapper::toModel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterModel> chaptersByStory(UUID storyId) {
        return chapters.findByStoryIdOrderByOrderAsc(storyId).stream().map(mapper::toModel).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResult<ChapterModel> chapters(ChapterFilters filters, PaginationRequest request) {
        Sort sort = Boolean.TRUE.equals(filters.getNewestFirst())
                ? Sort.by(Sort.Direction.DESC, "order") : Sort.by(Sort.Direction.ASC, "order");
        PageRequest pageRequest = PageRequest.of(request.getPageNumber(), request.getPageSize(), sort);
        Page<ChapterEntity> page = Boolean.TRUE.equals(filters.getVisibleOnly())
                ? chapters.findByStoryIdAndHiddenFalse(filters.getStoryId(), pageRequest)
                : chapters.findByStoryId(filters.getStoryId(), pageRequest);
        Page<ChapterModel> result = page.map(mapper::toModel);
        return PaginationResult.<ChapterModel>builder().content(result.getContent()).pageNumber(result.getNumber())
                .pageSize(result.getSize()).totalElements(result.getTotalElements()).totalPages(result.getTotalPages()).build();
    }

    @Override
    public boolean existsByStoryIdAndOrder(UUID storyId, Integer order) {
        return chapters.existsByStoryIdAndOrder(storyId, order);
    }

    @Override
    public boolean existsByStoryIdAndOrderAndIdNot(UUID storyId, Integer order, UUID chapterId) {
        return chapters.existsByStoryIdAndOrderAndIdNot(storyId, order, chapterId);
    }

    @Override
    public long countByStoryId(UUID storyId) {
        return chapters.countByStoryId(storyId);
    }

    @Override
    @Transactional(readOnly = true)
    public int maxOrderByStoryId(UUID storyId) {
        return chapters.findFirstByStoryIdOrderByOrderDesc(storyId).map(ChapterEntity::getOrder).orElse(0);
    }

    @Override
    @Transactional
    public void moveChapterOrder(UUID storyId, UUID chapterId, int targetOrder) {
        List<ChapterEntity> orderedChapters = new ArrayList<>(chapters.findByStoryIdOrderByOrderAsc(storyId));
        ChapterEntity movingChapter = orderedChapters.stream()
                .filter(chapter -> chapter.getId().equals(chapterId))
                .findFirst()
                .orElseThrow();
        orderedChapters.remove(movingChapter);
        orderedChapters.add(targetOrder - 1, movingChapter);

        chapters.moveOrdersToTemporaryValues(storyId);
        for (int index = 0; index < orderedChapters.size(); index++) {
            chapters.updateOrder(orderedChapters.get(index).getId(), index + 1);
        }
    }

    @Override
    public void delete(ChapterModel chapter) {
        chapters.delete(mapper.toEntity(chapter));
    }
}
