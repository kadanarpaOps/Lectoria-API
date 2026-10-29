package com.lectoria_api.stories.infrastructure.input.rest.controller;

import com.lectoria_api.common.domain.model.PageResponse;
import com.lectoria_api.common.infrastructure.cloudinary.CloudinaryFileUpdateUtil;
import com.lectoria_api.stories.domain.ports.input.StoryUseCases;
import com.lectoria_api.stories.infrastructure.output.view.ViewTrackingClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/story")
@RequiredArgsConstructor
public class StoryRestController {
    private static final Logger LOG = LoggerFactory.getLogger(StoryRestController.class);
    private final StoryUseCases stories;
    private final CloudinaryFileUpdateUtil cloudinary;
    private final ViewTrackingClient viewTracking;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> input) {
        Map<String, Object> story = stories.createStory(normalizeGenres(input));
        registerStory(story);
        return ResponseEntity.status(HttpStatus.CREATED).body(story);
    }
    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> createMultipart(@RequestParam Map<String, String> fields,
                                                                @RequestPart(value = "image", required = false) MultipartFile image) {
        Map<String, Object> input = new java.util.HashMap<>(fields);
        input = normalizeGenres(input);
        Object tagNames = input.remove("tagNames");
        if (tagNames != null) input.put("tags", parseTags(String.valueOf(tagNames)));
        Map<String, Object> created = stories.createStory(input);
        if (image != null && !image.isEmpty()) {
            CloudinaryFileUpdateUtil.ImageReference uploaded = cloudinary.uploadImage(image, "lectoria/stories");
            try {
                created = stories.setCover(String.valueOf(created.get("id")), uploaded.url(), uploaded.publicId());
            } catch (RuntimeException exception) {
                deleteCoverBestEffort(uploaded.publicId());
                throw exception;
            }
        }
        registerStory(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    @GetMapping("/id/{id}") public Map<String, Object> get(@PathVariable String id) { return stories.story(id); }
    @GetMapping("/title/{title}") public Map<String, Object> byTitle(@PathVariable String title) { return stories.storyByTitle(title); }
    @GetMapping public Map<String, Object> search(@RequestParam Map<String, String> filters) { return page(stories.search(filters, null)); }
    @GetMapping("/search") public Map<String, Object> searchPath(@RequestParam Map<String, String> filters) { return page(stories.search(filters, null)); }
    @GetMapping("/search/{userId}") public Map<String, Object> own(@PathVariable String userId, @RequestParam Map<String, String> filters) { return page(stories.search(filters, userId)); }
    @GetMapping("/last-modified/{userId}") public Map<String, Object> lastModified(@PathVariable String userId) { return stories.lastModified(userId); }
    @PutMapping("/update/{id}") public Map<String, Object> putUpdate(@PathVariable String id, @RequestBody Map<String, Object> input) { return stories.updateStory(id, normalizeGenres(input)); }
    @PatchMapping("/update/{id}") public Map<String, Object> update(@PathVariable String id, @RequestBody Map<String, Object> input) { return stories.updateStory(id, normalizeGenres(input)); }
    @PatchMapping("/toggle-hidden/{id}") public Map<String, Object> toggle(@PathVariable String id) { return stories.toggleHidden(id); }
    @DeleteMapping("/delete/{id}") public Map<String, Object> delete(@PathVariable String id) { return deleteStory(id); }
    @DeleteMapping("/{id}") public Map<String, Object> deleteAlias(@PathVariable String id) { return deleteStory(id); }

    @PatchMapping(value = "/cover/{id}", consumes = "multipart/form-data")
    public Map<String, Object> uploadCover(@PathVariable String id, @RequestPart("image") MultipartFile image) {
        Map<String, Object> story = stories.story(id);
        CloudinaryFileUpdateUtil.ImageReference uploaded = cloudinary.uploadImage(image, "lectoria/stories");
        Map<String, Object> updated;
        try {
            updated = stories.setCover(id, uploaded.url(), uploaded.publicId());
        } catch (RuntimeException exception) {
            deleteCoverBestEffort(uploaded.publicId());
            throw exception;
        }
        deleteCoverBestEffort(value(story, "coverPublicId", cloudinary.publicIdFromUrl(value(story, "coverUrl", null))));
        return updated;
    }
    @DeleteMapping("/cover/{id}")
    public Map<String, Object> deleteCover(@PathVariable String id) {
        Map<String, Object> story = stories.story(id);
        Map<String, Object> updated = stories.removeCover(id);
        deleteCoverBestEffort(value(story, "coverPublicId", cloudinary.publicIdFromUrl(value(story, "coverUrl", null))));
        return updated;
    }
    @PostMapping("/view/{storyId}/{userId}")
    public Map<String, Object> view(@PathVariable String storyId, @PathVariable String userId) {
        Map<String, Object> view = stories.createView(storyId, userId);
        viewTracking.track(storyId, userId);
        return view;
    }
    @PutMapping("/update/{id}/tags")
    public Map<String, Object> tags(@PathVariable String id, @RequestBody Map<String, List<String>> body) {
        stories.updateTags(id, body.getOrDefault("tagNames", List.of()));
        return Map.of("message", "Tags actualizados correctamente");
    }
    private Map<String, Object> page(PageResponse<Map<String, Object>> result) {
        return Map.of("data", result.getData(), "meta", result.getMetaData());
    }
    private void deleteCoverBestEffort(String publicId) {
        if (publicId == null || publicId.isBlank()) return;
        try { cloudinary.deleteImage(publicId); }
        catch (RuntimeException exception) { LOG.warn("Could not remove replaced story cover publicId '{}'", publicId, exception); }
    }
    private String value(Map<String, Object> map, String key, String fallback) {
        Object value = map.get(key);
        return value == null ? fallback : String.valueOf(value);
    }
    private Map<String, Object> normalizeGenres(Map<String, Object> input) {
        Map<String, Object> normalized = new java.util.HashMap<>(input);
        if (normalized.containsKey("genreName")) normalized.put("genreId", stories.genreByName(String.valueOf(normalized.remove("genreName"))).get("genreId"));
        if (normalized.containsKey("secondaryGenreName")) normalized.put("secondaryGenreId", stories.genreByName(String.valueOf(normalized.remove("secondaryGenreName"))).get("genreId"));
        if (normalized.containsKey("tagNames") && normalized.get("tagNames") instanceof List<?> list) {
            normalized.put("tags", list.stream().map(String::valueOf).toList());
        }
        return normalized;
    }
    private List<String> parseTags(String tags) {
        if (tags == null || tags.isBlank()) return List.of();
        try {
            com.fasterxml.jackson.databind.JsonNode json = new com.fasterxml.jackson.databind.ObjectMapper().readTree(tags);
            if (json.isArray()) {
                java.util.ArrayList<String> values = new java.util.ArrayList<>();
                json.forEach(tag -> values.add(tag.asText()));
                return values;
            }
        } catch (Exception ignored) {
        }
        return java.util.Arrays.stream(tags.split(",")).map(String::trim).filter(tag -> !tag.isEmpty()).toList();
    }
    private Map<String, Object> deleteStory(String id) {
        Map<String, Object> deleted = stories.deleteStory(id);
        viewTracking.removeStory(id);
        return Map.of("message", "Se eliminó de manera exitosa la historia con id: " + id);
    }
    private void registerStory(Map<String, Object> story) {
        viewTracking.registerStory(new ViewTrackingClient.MapStory(
                String.valueOf(story.get("id")), String.valueOf(story.get("title")), String.valueOf(story.get("userId"))));
    }
}
