package com.lectoria_api.stories.infrastructure.input.rest.controller;

import com.lectoria_api.stories.domain.ports.input.StoryUseCases;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/genre")
@RequiredArgsConstructor
public class GenreRestController {
    private final StoryUseCases stories;
    private final com.lectoria_api.stories.infrastructure.input.rest.mapper.StoryRestMapper mapper;
    @GetMapping public List<Map<String, Object>> all() { return stories.genres().stream().map(mapper::toResponse).toList(); }
    @GetMapping({"/id/{id}", "/{id}"}) public Map<String, Object> byId(@PathVariable UUID id) {
        return mapper.toResponse(stories.genreById(id));
    }
    @GetMapping("/name/{name}") public Map<String, Object> byName(@PathVariable String name) {
        return mapper.toResponse(stories.genreByName(name));
    }
}
