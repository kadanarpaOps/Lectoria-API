package com.lectoria_api.chapters.infrastructure.input.rest.controller;

import com.lectoria_api.chapters.domain.model.ChapterFilters;
import com.lectoria_api.chapters.domain.ports.input.ChapterUseCases;
import com.lectoria_api.chapters.infrastructure.input.rest.mapper.ChapterRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/chapter")
@RequiredArgsConstructor
public class ChapterRestController {
    private final ChapterUseCases chapters;
    private final ChapterRestMapper mapper;

    @GetMapping("/{storyId}")
    public Map<String, Object> list(@PathVariable UUID storyId, @ModelAttribute ChapterFilters filters) {
        filters.setStoryId(storyId);
        return mapper.toPageResponse(chapters.chapters(filters, false));
    }

    @GetMapping("/my-chapters/{storyId}")
    public Map<String, Object> owned(@PathVariable UUID storyId, @ModelAttribute ChapterFilters filters) {
        filters.setStoryId(storyId);
        return mapper.toPageResponse(chapters.chapters(filters, true));
    }

    @GetMapping("/id/{chapterId}")
    public Map<String, Object> get(@PathVariable UUID chapterId) {
        return mapper.toResponse(chapters.chapter(chapterId));
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> input) {
        UUID storyId = mapper.storyId(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(chapters.createChapter(storyId)));
    }

    @PatchMapping("/toggle-hidden/{id}")
    public Map<String, Object> toggle(@PathVariable UUID id) {
        return mapper.toResponse(chapters.toggleChapter(id));
    }

    @DeleteMapping("/{storyId}/chapter/{chapterId}")
    public Map<String, String> delete(@PathVariable UUID storyId, @PathVariable UUID chapterId) {
        chapters.deleteChapter(storyId, chapterId);
        return Map.of("message", "Chapter deleted successfully");
    }

    @PatchMapping("/update/{id}")
    public Map<String, Object> update(@PathVariable UUID id, @RequestBody Map<String, Object> input) {
        return mapper.toResponse(chapters.updateChapter(id, mapper.toModel(input)));
    }
}
