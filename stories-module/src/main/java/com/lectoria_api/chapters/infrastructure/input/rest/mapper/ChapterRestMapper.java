package com.lectoria_api.chapters.infrastructure.input.rest.mapper;

import com.lectoria_api.chapters.domain.model.ChapterModel;
import com.lectoria_api.chapters.domain.exceptions.InvalidChapterDataException;
import com.lectoria_api.common.domain.model.PageResponse;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class ChapterRestMapper {
    public ChapterModel toModel(Map<String, ?> input) {
        if (input == null) throw new InvalidChapterDataException("Chapter data is required");
        Object order = input.get("order");
        try {
            return ChapterModel.builder().title(string(input.get("title")))
                    .content(string(input.get("content"))).order(order == null ? null : Integer.valueOf(String.valueOf(order)))
                    .build();
        } catch (NumberFormatException exception) {
            throw new InvalidChapterDataException("Invalid chapter order");
        }
    }

    public UUID storyId(Map<String, ?> input) {
        if (input == null) return null;
        Object storyId = input.get("storyId");
        if (storyId == null) return null;
        try {
            return UUID.fromString(String.valueOf(storyId));
        } catch (IllegalArgumentException exception) {
            throw new InvalidChapterDataException("Invalid storyId");
        }
    }

    public Map<String, Object> toResponse(ChapterModel chapter) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", chapter.getId());
        response.put("title", chapter.getTitle());
        response.put("order", chapter.getOrder());
        response.put("content", chapter.getContent());
        response.put("hidden", chapter.getHidden());
        response.put("totalComments", chapter.getTotalComments());
        response.put("storyId", chapter.getStoryId());
        response.put("createdAt", chapter.getCreatedAt());
        response.put("updatedAt", chapter.getUpdatedAt());
        response.put("lastActivityAt", chapter.getLastActivityAt());
        return response;
    }

    public Map<String, Object> toPageResponse(PageResponse<ChapterModel> page) {
        List<Map<String, Object>> data = page.getData().stream().map(this::toResponse).toList();
        return Map.of("data", data, "meta", page.getMetaData());
    }

    private String string(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
