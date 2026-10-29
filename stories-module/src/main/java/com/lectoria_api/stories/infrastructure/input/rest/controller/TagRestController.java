package com.lectoria_api.stories.infrastructure.input.rest.controller;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.stories.domain.ports.input.StoryUseCases;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tag")
@RequiredArgsConstructor
public class TagRestController {
    private final StoryUseCases stories;
    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam(defaultValue = "") String name,
                                      @RequestParam(defaultValue = "20") int limit,
                                      @RequestParam(defaultValue = "0") int offset) {
        PageResponse<Map<String, Object>> page = stories.searchTags(name, limit, offset);
        return Map.of("data", page.getData(), "meta", page.getMetaData());
    }
}
