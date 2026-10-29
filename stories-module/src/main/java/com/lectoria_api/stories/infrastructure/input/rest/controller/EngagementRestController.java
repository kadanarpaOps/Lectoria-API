package com.lectoria_api.stories.infrastructure.input.rest.controller;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.stories.domain.model.FavoriteModel;
import com.lectoria_api.stories.domain.model.RatingModel;
import com.lectoria_api.stories.domain.model.StoryModel;
import com.lectoria_api.stories.domain.exceptions.InvalidStoryDataException;
import com.lectoria_api.stories.domain.ports.input.StoryUseCases;
import com.lectoria_api.stories.infrastructure.input.rest.mapper.StoryRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class EngagementRestController {
    private final StoryUseCases stories;
    private final StoryRestMapper mapper;

    @PostMapping("/api/rating/create")
    public Map<String, Object> rate(@RequestBody Map<String, Object> input) {
        if (input == null) throw new InvalidStoryDataException("Rating data is required");
        UUID storyId;
        BigDecimal score;
        try {
            storyId = UUID.fromString(String.valueOf(input.get("storyId")));
            score = new BigDecimal(String.valueOf(input.get("score")));
        } catch (IllegalArgumentException exception) {
            throw new InvalidStoryDataException("Invalid storyId or score");
        }
        return ratingResponse(stories.rate(storyId, score));
    }

    @GetMapping("/api/rating/exists/story/{storyId}")
    public Map<String, Object> rating(@PathVariable UUID storyId) {
        RatingModel rating = stories.rating(storyId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("data", rating == null ? null : ratingResponse(rating));
        return response;
    }

    @PostMapping("/api/favorite/add/{storyId}")
    public Map<String, Object> favorite(@PathVariable UUID storyId) {
        stories.favorite(storyId, true);
        return Map.of("message", "Favorite added successfully");
    }

    @DeleteMapping("/api/favorite/remove/story/{storyId}")
    public Map<String, Object> unfavorite(@PathVariable UUID storyId) {
        stories.favorite(storyId, false);
        return Map.of("message", "Favorite removed successfully");
    }

    @GetMapping("/api/favorite/exists/story/{storyId}")
    public Map<String, Object> isFavorite(@PathVariable UUID storyId) {
        return Map.of("data", stories.favoriteExists(storyId));
    }

    @GetMapping("/api/favorite/search")
    public Map<String, Object> favorites(@RequestParam(defaultValue = "0") int offset,
                                         @RequestParam(defaultValue = "20") int limit) {
        PageResponse<StoryModel> page = stories.favorites(offset / Math.max(limit, 1), limit);
        return mapper.toPageResponse(page);
    }

    private Map<String, Object> ratingResponse(RatingModel rating) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", rating.getId());
        response.put("storyId", rating.getStoryId());
        response.put("userId", rating.getUserId());
        response.put("score", rating.getScore());
        response.put("createdAt", rating.getCreatedAt());
        response.put("updatedAt", rating.getUpdatedAt());
        return response;
    }
}
