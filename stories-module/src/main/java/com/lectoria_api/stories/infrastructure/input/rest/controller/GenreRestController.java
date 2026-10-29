package com.lectoria_api.stories.infrastructure.input.rest.controller;

import com.lectoria_api.stories.domain.ports.input.StoryUseCases;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/genre")
@RequiredArgsConstructor
public class GenreRestController {
    private final StoryUseCases stories;
    @GetMapping public List<Map<String, Object>> all() { return stories.genres(); }
    @GetMapping({"/id/{id}", "/{id}"}) public Map<String, Object> byId(@PathVariable String id) { return stories.genreById(id); }
    @GetMapping("/name/{name}") public Map<String, Object> byName(@PathVariable String name) { return stories.genreByName(name); }
}
