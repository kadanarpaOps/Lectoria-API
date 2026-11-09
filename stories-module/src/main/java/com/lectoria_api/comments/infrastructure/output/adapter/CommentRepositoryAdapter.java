package com.lectoria_api.comments.infrastructure.output.adapter;

import com.lectoria_api.comments.domain.model.CommentFilters;
import com.lectoria_api.comments.domain.model.CommentModel;
import com.lectoria_api.comments.domain.ports.output.CommentRepositoryPort;
import com.lectoria_api.comments.infrastructure.output.persistence.entities.CommentEntity;
import com.lectoria_api.comments.infrastructure.output.persistence.mapper.CommentPersistenceMapper;
import com.lectoria_api.comments.infrastructure.output.persistence.repository.JpaCommentLikeRepository;
import com.lectoria_api.comments.infrastructure.output.persistence.repository.JpaCommentRepository;
import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.common.domain.model.PaginationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CommentRepositoryAdapter implements CommentRepositoryPort {
    private final JpaCommentRepository comments;
    private final JpaCommentLikeRepository likes;
    private final CommentPersistenceMapper mapper;

    @Override
    @Transactional
    public CommentModel save(CommentModel comment) {
        return mapper.toModel(comments.save(mapper.toEntity(comment)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentModel> findById(UUID id) {
        return comments.findById(id).map(mapper::toModel);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResult<CommentModel> comments(CommentFilters filters, PaginationRequest request) {
        Page<CommentEntity> page = filters.getStoryId() != null
                ? comments.findByStoryIdOrderByCreatedAtDesc(filters.getStoryId(),
                PageRequest.of(request.getPageNumber(), request.getPageSize()))
                : comments.findByChapterIdOrderByCreatedAtDesc(filters.getChapterId(),
                PageRequest.of(request.getPageNumber(), request.getPageSize()));
        Page<CommentModel> result = page.map(mapper::toModel);
        return PaginationResult.<CommentModel>builder().content(result.getContent()).pageNumber(result.getNumber())
                .pageSize(result.getSize()).totalElements(result.getTotalElements()).totalPages(result.getTotalPages()).build();
    }

    @Override
    public void delete(CommentModel comment) {
        comments.deleteById(comment.getId());
    }

    @Override
    public boolean likeExists(UUID commentId, UUID userId) {
        return likes.findByCommentIdAndUserId(commentId, userId).isPresent();
    }

    @Override
    @Transactional
    public void saveLike(UUID commentId, UUID userId) {
        likes.save(mapper.toLikeEntity(commentId, userId));
    }

    @Override
    public void deleteLike(UUID commentId, UUID userId) {
        likes.findByCommentIdAndUserId(commentId, userId).ifPresent(likes::delete);
    }

    @Override
    public long countLikes(UUID commentId) {
        return likes.countByCommentId(commentId);
    }

    @Override
    public long countByStoryId(UUID storyId) {
        return comments.countByStoryId(storyId);
    }

    @Override
    public long countByChapterId(UUID chapterId) {
        return comments.countByChapterId(chapterId);
    }
}
