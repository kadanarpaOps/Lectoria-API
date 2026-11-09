package com.lectoria_api.comments.domain.ports.output;

import com.lectoria_api.comments.domain.model.CommentFilters;
import com.lectoria_api.comments.domain.model.CommentModel;
import com.lectoria_api.common.domain.model.PaginationRequest;
import com.lectoria_api.common.domain.model.PaginationResult;

import java.util.Optional;
import java.util.UUID;

public interface CommentRepositoryPort {
    CommentModel save(CommentModel comment);
    Optional<CommentModel> findById(UUID id);
    PaginationResult<CommentModel> comments(CommentFilters filters, PaginationRequest request);
    void delete(CommentModel comment);
    boolean likeExists(UUID commentId, UUID userId);
    void saveLike(UUID commentId, UUID userId);
    void deleteLike(UUID commentId, UUID userId);
    long countLikes(UUID commentId);
    long countByStoryId(UUID storyId);
    long countByChapterId(UUID chapterId);
}
