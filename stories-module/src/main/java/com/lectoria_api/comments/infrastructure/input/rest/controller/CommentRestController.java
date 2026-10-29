package com.lectoria_api.comments.infrastructure.input.rest.controller;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.comments.domain.ports.input.CommentUseCases;
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
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentRestController {
    private final CommentUseCases comments;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "El comentario ha sido creado de forma correcta",
                "comment", comments.createComment(input)));
    }

    @DeleteMapping("/{commentId}")
    public Map<String, Object> delete(@PathVariable String commentId) {
        Map<String, Object> deleted = comments.comment(commentId);
        comments.deleteComment(commentId);
        return Map.of("comment", deleted, "message", "El comentario de id " + deleted.get("id") + " fue eliminado de manera exitosa");
    }

    @PatchMapping("/update/{commentId}")
    public Map<String, Object> update(@PathVariable String commentId, @RequestBody Map<String, Object> input) {
        return Map.of("comment", comments.updateComment(commentId, input), "message", "El comentario ha sido actualizado exitosamente");
    }

    @GetMapping("/search/story/{storyId}")
    public Map<String, Object> byStory(@PathVariable String storyId, @RequestParam Map<String, String> query) {
        return page(comments.comments(storyId, true, query));
    }

    @GetMapping("/search/chapter/{chapterId}")
    public Map<String, Object> byChapter(@PathVariable String chapterId, @RequestParam Map<String, String> query) {
        return page(comments.comments(chapterId, false, query));
    }

    @PatchMapping("/toggle-like/{commentId}/{userId}")
    public Map<String, Object> toggleLike(@PathVariable String commentId, @PathVariable String userId) {
        return Map.of("comment", comments.toggleLike(commentId, userId), "message", "Like actualizado exitosamente");
    }

    private Map<String, Object> page(PageResponse<Map<String, Object>> result) {
        return Map.of("data", result.getData(), "meta", result.getMetaData());
    }
}
