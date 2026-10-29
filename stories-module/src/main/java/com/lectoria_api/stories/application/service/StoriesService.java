package com.lectoria_api.stories.application.service;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.stories.domain.ports.input.StoryUseCases;
import com.lectoria_api.stories.domain.ports.output.StoryRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StoriesService implements StoryUseCases {
    private final StoryRepositoryPort stories;

    @Override public Map<String, Object> createStory(Map<String, Object> input) { return stories.create(input); }
    @Override public Map<String, Object> story(String id) { return stories.findById(id); }
    @Override public Map<String, Object> storyByTitle(String title) { return stories.findByTitle(title); }
    @Override public Map<String, Object> updateStory(String id, Map<String, Object> input) { return stories.update(id, input); }
    @Override public Map<String, Object> deleteStory(String id) { return stories.delete(id); }
    @Override public Map<String, Object> toggleHidden(String id) { return stories.toggleHidden(id); }
    @Override public PageResponse<Map<String, Object>> search(Map<String, String> filters, String ownerId) { return stories.search(filters, ownerId); }
    @Override public Map<String, Object> lastModified(String userId) { return stories.lastModified(userId); }
    @Override public List<Map<String, Object>> genres() { return stories.genres(); }
    @Override public Map<String, Object> genreById(String id) { return stories.genreById(id); }
    @Override public Map<String, Object> genreByName(String name) { return stories.genreByName(name); }
    @Override public void updateTags(String id, List<String> tags) { stories.updateTags(id, tags); }
    @Override public PageResponse<Map<String, Object>> searchTags(String name, int limit, int offset) { return stories.searchTags(name, limit, offset); }
    @Override public Map<String, Object> rate(Map<String, Object> input) { return stories.rate(input); }
    @Override public Map<String, Object> rating(String storyId, String userId) { return stories.rating(storyId, userId); }
    @Override public Map<String, Object> favorite(String storyId, String userId, boolean add) { return stories.favorite(storyId, userId, add); }
    @Override public boolean favoriteExists(String storyId, String userId) { return stories.favoriteExists(storyId, userId); }
    @Override public PageResponse<Map<String, Object>> favorites(String userId, Map<String, String> filters) { return stories.favorites(userId, filters); }
    @Override public Map<String, Object> createView(String storyId, String userId) { return stories.createView(storyId, userId); }
    @Override public Map<String, Object> setCover(String id, String url, String publicId) { return stories.setCover(id, url, publicId); }
    @Override public Map<String, Object> removeCover(String id) { return stories.removeCover(id); }
}
