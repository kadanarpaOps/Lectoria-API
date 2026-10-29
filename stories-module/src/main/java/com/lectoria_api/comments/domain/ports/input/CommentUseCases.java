package com.lectoria_api.comments.domain.ports.input;

import com.lectoria_api.comments.domain.model.CommentFilters;
import com.lectoria_api.comments.domain.model.CommentModel;
import com.lectoria_api.common.domain.model.PageResponse;

import java.util.UUID;

public interface CommentUseCases {
    CommentModel createComment(CommentModel comment);
    CommentModel comment(UUID id);
    PageResponse<CommentModel> comments(CommentFilters filters);
    CommentModel updateComment(UUID id, String content);
    void deleteComment(UUID id);
    CommentModel toggleLike(UUID commentId);
}
