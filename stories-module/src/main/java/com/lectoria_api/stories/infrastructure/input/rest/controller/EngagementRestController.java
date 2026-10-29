package com.lectoria_api.stories.infrastructure.input.rest.controller;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.stories.domain.ports.input.StoryUseCases;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class EngagementRestController {
    private final StoryUseCases stories;

    @PostMapping("/api/rating/create")
    public Map<String, Object> rate(@RequestBody Map<String, Object> input) { return stories.rate(input); }
    @GetMapping("/api/rating/exists/{userId}/story/{storyId}")
    public Map<String, Object> rating(@PathVariable String storyId, @PathVariable String userId) {
        try { return Map.of("data", stories.rating(storyId, userId)); }
        catch (org.springframework.web.server.ResponseStatusException exception) {
            if (exception.getStatusCode().value() == 404) return java.util.Collections.singletonMap("data", null);
            throw exception;
        }
    }
    @PostMapping("/api/favorite/add")
    public Map<String, Object> favorite(@RequestBody Map<String, String> input) {
        stories.favorite(input.get("storyId"), input.get("userId"), true);
        return Map.of("message", "Favorite added successfully");
    }
    @DeleteMapping("/api/favorite/remove/{userId}/story/{storyId}")
    public Map<String, Object> unfavorite(@PathVariable String storyId, @PathVariable String userId) {
        stories.favorite(storyId, userId, false);
        return Map.of("message", "Favorite removed successfully");
    }
    @GetMapping("/api/favorite/exists/{userId}/story/{storyId}")
    public Map<String, Object> isFavorite(@PathVariable String storyId, @PathVariable String userId) {
        return Map.of("data", stories.favoriteExists(storyId, userId));
    }
    @GetMapping("/api/favorite/search/{userId}")
    public Map<String, Object> favorites(@PathVariable String userId, @RequestParam Map<String, String> filters) {
        PageResponse<Map<String, Object>> page = stories.favorites(userId, filters);
        return Map.of("data", page.getData(), "meta", page.getMetaData());
    }
}
