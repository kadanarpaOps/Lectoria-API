package com.lectoria_api.comments.domain.ports.output;

import com.lectoria_api.common.domain.model.PageResponse;

import java.util.Map;

public interface CommentRepositoryPort {
    Map<String, Object> createComment(Map<String, Object> input);
    Map<String, Object> comment(String id);
    PageResponse<Map<String, Object>> comments(String parentId, boolean byStory, Map<String, String> query);
    Map<String, Object> updateComment(String id, Map<String, Object> input);
    void deleteComment(String id);
    Map<String, Object> toggleLike(String commentId, String userId);
}
