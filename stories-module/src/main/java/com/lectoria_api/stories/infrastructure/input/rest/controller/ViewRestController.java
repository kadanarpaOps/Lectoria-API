package com.lectoria_api.stories.infrastructure.input.rest.controller;

import com.lectoria_api.stories.domain.ports.input.StoryUseCases;
import com.lectoria_api.stories.domain.exceptions.InvalidStoryDataException;
import com.lectoria_api.stories.infrastructure.output.view.ViewTrackingClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/view")
@RequiredArgsConstructor
public class ViewRestController {
    private final StoryUseCases stories;
    private final ViewTrackingClient tracking;

    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody Map<String, String> body) {
        if (body == null || body.get("storyId") == null) throw new InvalidStoryDataException("storyId is required");
        UUID storyId;
        try {
            storyId = UUID.fromString(body.get("storyId"));
        } catch (IllegalArgumentException exception) {
            throw new InvalidStoryDataException("Invalid storyId");
        }
        var view = stories.createView(storyId);
        tracking.track(storyId.toString(), view.getUserId().toString());
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", view.getId());
        response.put("storyId", view.getStoryId());
        response.put("userId", view.getUserId());
        response.put("createdAt", view.getCreatedAt());
        response.put("updatedAt", view.getUpdatedAt());
        return response;
    }
}
