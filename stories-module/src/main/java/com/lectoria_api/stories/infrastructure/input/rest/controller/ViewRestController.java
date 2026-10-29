package com.lectoria_api.stories.infrastructure.input.rest.controller;

import com.lectoria_api.stories.domain.ports.input.StoryUseCases;
import com.lectoria_api.stories.infrastructure.output.view.ViewTrackingClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/view")
@RequiredArgsConstructor
public class ViewRestController {
    private final StoryUseCases stories;
    private final ViewTrackingClient tracking;

    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody Map<String, String> body) {
        Map<String, Object> view = stories.createView(body.get("storyId"), body.get("userId"));
        tracking.track(body.get("storyId"), body.get("userId"));
        return view;
    }
}
