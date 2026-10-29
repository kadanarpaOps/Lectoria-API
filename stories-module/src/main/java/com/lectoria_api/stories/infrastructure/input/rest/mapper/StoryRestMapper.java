package com.lectoria_api.stories.infrastructure.input.rest.mapper;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.stories.domain.model.BasicInfoModel;
import com.lectoria_api.stories.domain.model.StoryModel;
import com.lectoria_api.stories.domain.model.StoryFilters;
import com.lectoria_api.stories.domain.exceptions.InvalidStoryDataException;
import com.lectoria_api.stories.infrastructure.input.rest.dto.StoryFiltersRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class StoryRestMapper {
    public StoryFilters toFilters(StoryFiltersRequest request) {
        return StoryFilters.builder().offset(request.getOffset()).limit(request.getLimit())
                .title(request.getTitle()).description(request.getDescription())
                .genreName(request.getGenreName()).secondaryGenreName(request.getSecondaryGenreName())
                .genreId(request.getGenreId()).status(request.getStatus()).hidden(request.getHidden())
                .newestFirst(request.getNewestFirst()).tagNames(request.getTagNames()).build();
    }

    public StoryModel toModel(Map<String, ?> input) {
        if (input == null) throw new InvalidStoryDataException("Story data is required");
        return StoryModel.builder()
                .title(string(input.get("title")))
                .description(string(input.get("description")))
                .genre(basicInfo(input, "genre", "genreId", "genreName"))
                .secondaryGenre(basicInfo(input, "secondaryGenre", "secondaryGenreId", "secondaryGenreName"))
                .status(string(input.get("status")))
                .hidden(bool(input.get("hidden")))
                .tags(tags(input.get("tags") == null ? input.get("tagNames") : input.get("tags")))
                .build();
    }

    public Map<String, Object> toResponse(StoryModel story) {
        if (story == null) return null;
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", story.getId());
        response.put("title", story.getTitle());
        response.put("coverUrl", story.getCoverUrl());
        response.put("coverPublicId", story.getCoverPublicId());
        response.put("description", story.getDescription());
        response.put("totalRating", story.getTotalRating());
        response.put("ratingSum", story.getRatingSum());
        response.put("ratingCount", story.getRatingCount());
        response.put("totalChapters", story.getTotalChapters());
        response.put("hidden", story.getHidden());
        response.put("status", story.getStatus());
        response.put("totalViews", story.getTotalViews());
        response.put("userId", story.getUserId());
        response.put("totalFavorites", story.getTotalFavorites());
        response.put("totalComments", story.getTotalComments());
        response.put("genre", toResponse(story.getGenre()));
        response.put("secondaryGenre", toResponse(story.getSecondaryGenre()));
        response.put("tags", story.getTags() == null ? List.of() : story.getTags().stream().map(this::toResponse).toList());
        response.put("createdAt", story.getCreatedAt());
        response.put("updatedAt", story.getUpdatedAt());
        response.put("lastActivityAt", story.getLastActivityAt());
        return response;
    }

    public Map<String, Object> toResponse(BasicInfoModel basicInfo) {
        return basicInfo == null ? null : Map.of("id", basicInfo.getId(), "name", basicInfo.getName());
    }

    public Map<String, Object> toResponse(Object model) {
        if (model instanceof StoryModel story) return toResponse(story);
        if (model instanceof BasicInfoModel basicInfo) return toResponse(basicInfo);
        if (model == null) return null;
        throw new IllegalArgumentException("Unsupported stories response model: " + model.getClass().getName());
    }

    public Map<String, Object> toPageResponse(PageResponse<?> page) {
        List<Object> data = page.getData().stream().map(item -> (Object) toResponse(item)).toList();
        return Map.of("data", data, "meta", page.getMetaData());
    }

    private BasicInfoModel basicInfo(Map<String, ?> input, String prefix, String idKey, String nameKey) {
        Object value = input.get(prefix);
        if (value instanceof Map<?, ?> map) {
            return BasicInfoModel.builder().id(uuid(map.get("id"))).name(string(map.get("name"))).build();
        }
        String id = string(input.get(idKey));
        String name = string(input.get(nameKey));
        if (name == null && value instanceof String text) name = text;
        if (id == null && name == null) return null;
        return BasicInfoModel.builder().id(uuid(id)).name(name).build();
    }

    private List<BasicInfoModel> tags(Object value) {
        if (!(value instanceof List<?> values)) return null;
        List<BasicInfoModel> tags = new ArrayList<>();
        for (Object tag : values) {
            if (tag instanceof Map<?, ?> map) {
                tags.add(BasicInfoModel.builder().id(uuid(map.get("id"))).name(string(map.get("name"))).build());
            } else {
                tags.add(BasicInfoModel.builder().name(string(tag)).build());
            }
        }
        return tags;
    }

    private String string(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Boolean bool(Object value) {
        return value == null ? null : value instanceof Boolean booleanValue
                ? booleanValue : Boolean.valueOf(String.valueOf(value));
    }

    private java.util.UUID uuid(Object value) {
        if (value == null) return null;
        if (value instanceof java.util.UUID id) return id;
        try {
            return java.util.UUID.fromString(String.valueOf(value));
        } catch (IllegalArgumentException exception) {
            throw new InvalidStoryDataException("Invalid genre or tag identifier");
        }
    }
}
