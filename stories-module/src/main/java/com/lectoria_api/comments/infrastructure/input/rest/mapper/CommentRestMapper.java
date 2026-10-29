package com.lectoria_api.comments.infrastructure.input.rest.mapper;

import com.lectoria_api.comments.domain.model.CommentModel;
import com.lectoria_api.comments.domain.exceptions.InvalidCommentDataException;
import com.lectoria_api.common.domain.model.PageResponse;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class CommentRestMapper {
    public CommentModel toModel(Map<String, ?> input) {
        if (input == null) throw new InvalidCommentDataException("Comment data is required");
        return CommentModel.builder().username(string(input.get("username"))).content(string(input.get("content")))
                .storyId(uuid(input.get("storyId"))).chapterId(uuid(input.get("chapterId"))).build();
    }

    public Map<String, Object> toResponse(CommentModel comment) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", comment.getId());
        response.put("username", comment.getUsername());
        response.put("userId", comment.getUserId());
        response.put("content", comment.getContent());
        response.put("likes", comment.getLikes());
        response.put("storyId", comment.getStoryId());
        response.put("chapterId", comment.getChapterId());
        response.put("createdAt", comment.getCreatedAt());
        response.put("updatedAt", comment.getUpdatedAt());
        return response;
    }

    public Map<String, Object> toPageResponse(PageResponse<CommentModel> page) {
        List<Map<String, Object>> data = page.getData().stream().map(this::toResponse).toList();
        return Map.of("data", data, "meta", page.getMetaData());
    }

    private String string(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private UUID uuid(Object value) {
        if (value == null) return null;
        try {
            return UUID.fromString(String.valueOf(value));
        } catch (IllegalArgumentException exception) {
            throw new InvalidCommentDataException("Invalid storyId or chapterId");
        }
    }
}
