package com.lectoria_api.comments.application.service;

import com.lectoria_api.comments.domain.ports.input.CommentUseCases;
import com.lectoria_api.comments.domain.ports.output.CommentRepositoryPort;
import com.lectoria_api.common.domain.model.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentService implements CommentUseCases {
    private final CommentRepositoryPort comments;

    @Override public Map<String, Object> createComment(Map<String, Object> input) { return comments.createComment(input); }
    @Override public Map<String, Object> comment(String id) { return comments.comment(id); }
    @Override public PageResponse<Map<String, Object>> comments(String parentId, boolean byStory, Map<String, String> query) {
        return comments.comments(parentId, byStory, query);
    }
    @Override public Map<String, Object> updateComment(String id, Map<String, Object> input) { return comments.updateComment(id, input); }
    @Override public void deleteComment(String id) { comments.deleteComment(id); }
    @Override public Map<String, Object> toggleLike(String commentId, String userId) { return comments.toggleLike(commentId, userId); }
}
