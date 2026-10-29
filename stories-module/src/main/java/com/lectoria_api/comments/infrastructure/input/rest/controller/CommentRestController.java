package com.lectoria_api.comments.infrastructure.input.rest.controller;

import com.lectoria_api.comments.domain.model.CommentFilters;
import com.lectoria_api.comments.domain.ports.input.CommentUseCases;
import com.lectoria_api.comments.infrastructure.input.rest.mapper.CommentRestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentRestController {
    private final CommentUseCases comments;
    private final CommentRestMapper mapper;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "El comentario ha sido creado de forma correcta",
                "comment", mapper.toResponse(comments.createComment(mapper.toModel(input)))));
    }

    @DeleteMapping("/{commentId}")
    public Map<String, Object> delete(@PathVariable UUID commentId) {
        var deleted = comments.comment(commentId);
        comments.deleteComment(commentId);
        return Map.of("comment", mapper.toResponse(deleted),
                "message", "El comentario de id " + deleted.getId() + " fue eliminado de manera exitosa");
    }

    @PatchMapping("/update/{commentId}")
    public Map<String, Object> update(@PathVariable UUID commentId, @RequestBody Map<String, Object> input) {
        Object content = input.get("content");
        return Map.of("comment", mapper.toResponse(comments.updateComment(commentId,
                        content == null ? null : String.valueOf(content))),
                "message", "El comentario ha sido actualizado exitosamente");
    }

    @GetMapping("/search/story/{storyId}")
    public Map<String, Object> byStory(@PathVariable UUID storyId, @ModelAttribute CommentFilters filters) {
        filters.setStoryId(storyId);
        return mapper.toPageResponse(comments.comments(filters));
    }

    @GetMapping("/search/chapter/{chapterId}")
    public Map<String, Object> byChapter(@PathVariable UUID chapterId, @ModelAttribute CommentFilters filters) {
        filters.setChapterId(chapterId);
        return mapper.toPageResponse(comments.comments(filters));
    }

    @PatchMapping("/toggle-like/{commentId}")
    public Map<String, Object> toggleLike(@PathVariable UUID commentId) {
        return Map.of("comment", mapper.toResponse(comments.toggleLike(commentId)),
                "message", "Like actualizado exitosamente");
    }
}
