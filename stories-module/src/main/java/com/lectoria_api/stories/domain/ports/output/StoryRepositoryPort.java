package com.lectoria_api.stories.domain.ports.output;

import com.lectoria_api.common.domain.model.PageResponse;

import java.util.List;
import java.util.Map;

public interface StoryRepositoryPort {
    Map<String, Object> create(Map<String, Object> input);
    Map<String, Object> findById(String id);
    Map<String, Object> findByTitle(String title);
    Map<String, Object> update(String id, Map<String, Object> input);
    Map<String, Object> delete(String id);
    Map<String, Object> toggleHidden(String id);
    PageResponse<Map<String, Object>> search(Map<String, String> filters, String ownerId);
    Map<String, Object> lastModified(String userId);
    List<Map<String, Object>> genres();
    Map<String, Object> genreById(String id);
    Map<String, Object> genreByName(String name);
    void updateTags(String storyId, List<String> tagNames);
    PageResponse<Map<String, Object>> searchTags(String name, int limit, int offset);
    Map<String, Object> rate(Map<String, Object> input);
    Map<String, Object> rating(String storyId, String userId);
    Map<String, Object> favorite(String storyId, String userId, boolean add);
    boolean favoriteExists(String storyId, String userId);
    PageResponse<Map<String, Object>> favorites(String userId, Map<String, String> filters);
    Map<String, Object> createView(String storyId, String userId);
    Map<String, Object> setCover(String id, String url, String publicId);
    Map<String, Object> removeCover(String id);
}
