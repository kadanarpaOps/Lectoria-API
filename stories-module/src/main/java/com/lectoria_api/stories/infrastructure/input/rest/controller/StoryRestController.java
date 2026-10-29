package com.lectoria_api.stories.infrastructure.input.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lectoria_api.common.infrastructure.cloudinary.CloudinaryFileUpdateUtil;
import com.lectoria_api.stories.domain.model.StoryModel;
import com.lectoria_api.stories.domain.exceptions.InvalidStoryDataException;
import com.lectoria_api.stories.domain.ports.input.StoryUseCases;
import com.lectoria_api.stories.infrastructure.input.rest.dto.StoryFiltersRequest;
import com.lectoria_api.stories.infrastructure.input.rest.mapper.StoryRestMapper;
import com.lectoria_api.stories.infrastructure.output.view.ViewTrackingClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/story")
@RequiredArgsConstructor
public class StoryRestController {
    private static final Logger LOG = LoggerFactory.getLogger(StoryRestController.class);
    private final StoryUseCases stories;
    private final CloudinaryFileUpdateUtil cloudinary;
    private final ViewTrackingClient viewTracking;
    private final StoryRestMapper mapper;
    private final ObjectMapper objectMapper;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> input) {
        StoryModel created = stories.createStory(mapper.toModel(input));
        Map<String, Object> response = mapper.toResponse(created);
        registerStory(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> createMultipart(@RequestParam Map<String, String> fields,
                                                                @RequestPart(value = "image", required = false) MultipartFile image) {
        Map<String, Object> input = new java.util.HashMap<>(fields);
        if (fields.containsKey("tagNames")) input.put("tags", parseTags(fields.get("tagNames")));
        StoryModel created = stories.createStory(mapper.toModel(input));
        if (image != null && !image.isEmpty()) {
            CloudinaryFileUpdateUtil.ImageReference uploaded = cloudinary.uploadImage(image, "lectoria/stories");
            try {
                created = stories.setCover(created.getId(), uploaded.url(), uploaded.publicId());
            } catch (RuntimeException exception) {
                deleteCoverBestEffort(uploaded.publicId());
                throw exception;
            }
        }
        registerStory(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(created));
    }

    @GetMapping("/id/{id}")
    public Map<String, Object> get(@PathVariable UUID id) {
        return mapper.toResponse(stories.story(id));
    }

    @GetMapping("/title/{title}")
    public Map<String, Object> byTitle(@PathVariable String title) {
        return mapper.toResponse(stories.storyByTitle(title));
    }

    @GetMapping
    public Map<String, Object> search(@ModelAttribute StoryFiltersRequest filters) {
        return mapper.toPageResponse(stories.search(mapper.toFilters(filters)));
    }

    @GetMapping("/search")
    public Map<String, Object> searchPath(@ModelAttribute StoryFiltersRequest filters) {
        return mapper.toPageResponse(stories.search(mapper.toFilters(filters)));
    }

    @GetMapping("/mine")
    public Map<String, Object> own(@ModelAttribute StoryFiltersRequest filters) {
        return mapper.toPageResponse(stories.searchOwnStories(mapper.toFilters(filters)));
    }

    @GetMapping("/last-modified")
    public Map<String, Object> lastModified() {
        return mapper.toResponse(stories.lastModified());
    }

    @PutMapping("/update/{id}")
    public Map<String, Object> putUpdate(@PathVariable UUID id, @RequestBody Map<String, Object> input) {
        return mapper.toResponse(stories.updateStory(id, mapper.toModel(input)));
    }

    @PatchMapping("/update/{id}")
    public Map<String, Object> update(@PathVariable UUID id, @RequestBody Map<String, Object> input) {
        return mapper.toResponse(stories.updateStory(id, mapper.toModel(input)));
    }

    @PatchMapping("/toggle-hidden/{id}")
    public Map<String, Object> toggle(@PathVariable UUID id) {
        return mapper.toResponse(stories.toggleHidden(id));
    }

    @DeleteMapping("/delete/{id}")
    public Map<String, Object> delete(@PathVariable UUID id) {
        return deleteStory(id);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteAlias(@PathVariable UUID id) {
        return deleteStory(id);
    }

    @PatchMapping(value = "/cover/{id}", consumes = "multipart/form-data")
    public Map<String, Object> uploadCover(@PathVariable UUID id, @RequestPart("image") MultipartFile image) {
        stories.ensureOwnedStory(id);
        StoryModel story = stories.story(id);
        CloudinaryFileUpdateUtil.ImageReference uploaded = cloudinary.uploadImage(image, "lectoria/stories");
        StoryModel updated;
        try {
            updated = stories.setCover(id, uploaded.url(), uploaded.publicId());
        } catch (RuntimeException exception) {
            deleteCoverBestEffort(uploaded.publicId());
            throw exception;
        }
        deleteCoverBestEffort(publicId(story));
        return mapper.toResponse(updated);
    }

    @DeleteMapping("/cover/{id}")
    public Map<String, Object> deleteCover(@PathVariable UUID id) {
        stories.ensureOwnedStory(id);
        StoryModel story = stories.story(id);
        StoryModel updated = stories.removeCover(id);
        deleteCoverBestEffort(publicId(story));
        return mapper.toResponse(updated);
    }

    @PostMapping("/view/{storyId}")
    public Map<String, Object> view(@PathVariable UUID storyId) {
        var view = stories.createView(storyId);
        viewTracking.track(storyId.toString(), view.getUserId().toString());
        return Map.of("id", view.getId(), "storyId", view.getStoryId(), "userId", view.getUserId(),
                "createdAt", view.getCreatedAt(), "updatedAt", view.getUpdatedAt());
    }

    @PutMapping("/update/{id}/tags")
    public Map<String, Object> tags(@PathVariable UUID id, @RequestBody Map<String, List<String>> body) {
        stories.updateTags(id, body.getOrDefault("tagNames", List.of()));
        return Map.of("message", "Tags actualizados correctamente");
    }

    private void deleteCoverBestEffort(String publicId) {
        if (publicId == null || publicId.isBlank()) return;
        try {
            cloudinary.deleteImage(publicId);
        } catch (RuntimeException exception) {
            LOG.warn("Could not remove replaced story cover publicId '{}'", publicId, exception);
        }
    }

    private String publicId(StoryModel story) {
        return story.getCoverPublicId() == null
                ? cloudinary.publicIdFromUrl(story.getCoverUrl()) : story.getCoverPublicId();
    }

    private List<String> parseTags(String tagNames) {
        if (tagNames == null || tagNames.isBlank()) return List.of();
        if (!tagNames.trim().startsWith("[")) {
            return Arrays.stream(tagNames.split(",")).map(String::trim).filter(tag -> !tag.isEmpty()).toList();
        }
        try {
            return objectMapper.readValue(tagNames, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, String.class));
        } catch (JsonProcessingException exception) {
            throw new InvalidStoryDataException("Invalid tagNames JSON");
        }
    }

    private Map<String, Object> deleteStory(UUID id) {
        StoryModel deleted = stories.deleteStory(id);
        viewTracking.removeStory(id.toString());
        return Map.of("message", "Se eliminó de manera exitosa la historia con id: " + deleted.getId());
    }

    private void registerStory(StoryModel story) {
        viewTracking.registerStory(new ViewTrackingClient.MapStory(
                story.getId().toString(), story.getTitle(), story.getUserId().toString()));
    }
}
