package com.lectoria_api.chapters.infrastructure.input.rest.controller;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.chapters.domain.ports.input.ChapterUseCases;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chapter")
@RequiredArgsConstructor
public class ChapterRestController {
    private final ChapterUseCases chapters;

    @GetMapping("/{storyId}")
    public Map<String, Object> list(@PathVariable String storyId, @RequestParam Map<String, String> query) {
        return page(chapters.chapters(storyId, query, false));
    }

    @GetMapping("/my-chapters/{storyId}")
    public Map<String, Object> owned(@PathVariable String storyId, @RequestParam Map<String, String> query) {
        return page(chapters.chapters(storyId, query, true));
    }

    @GetMapping("/id/{chapterId}")
    public Map<String, Object> get(@PathVariable String chapterId) { return chapters.chapter(chapterId); }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chapters.createChapter(input));
    }

    @PatchMapping("/toggle-hidden/{id}")
    public Map<String, Object> toggle(@PathVariable String id) { return chapters.toggleChapter(id); }

    @DeleteMapping("/{storyId}/chapter/{chapterId}")
    public Map<String, String> delete(@PathVariable String storyId, @PathVariable String chapterId) {
        chapters.deleteChapter(storyId, chapterId);
        return Map.of("message", "Chapter deleted successfully");
    }

    @PatchMapping("/update/{id}")
    public Map<String, Object> update(@PathVariable String id, @RequestBody Map<String, Object> input) {
        return chapters.updateChapter(id, input);
    }

    private Map<String, Object> page(PageResponse<Map<String, Object>> result) {
        return Map.of("data", result.getData(), "meta", result.getMetaData());
    }
}
