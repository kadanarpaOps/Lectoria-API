package com.lectoria_api.requests.infrastructure;

import com.lectoria_api.requests.application.RequestService;
import com.lectoria_api.requests.infrastructure.persistence.CoauthorEntity;
import com.lectoria_api.requests.infrastructure.persistence.RequestEntity;
import com.lectoria_api.stories.domain.model.StoryModel;
import com.lectoria_api.users.domain.ports.input.SharedUserUseCases;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService service;
    private final SharedUserUseCases users;

    @PostMapping("/coauthor/{storyId}")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestEntity requestCoauthor(@PathVariable UUID storyId) {
        return service.requestCoauthor(storyId);
    }

    @PostMapping("/coauthor/{storyId}/invite")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestEntity invite(@PathVariable UUID storyId, @Valid @RequestBody InviteRequest body) {
        return service.inviteCoauthor(storyId, body.username());
    }

    @GetMapping
    public List<RequestResponse> myRequests() {
        return service.myRequests().stream().map(request -> new RequestResponse(
                request.getId(), request.getType(), request.getStatus(), request.getSourceUserId(),
                request.getTargetUserId(), request.getStoryId(), request.getCoauthorUserId(),
                request.getCreatedAt(), username(request.getSourceUserId()),
                username(request.getTargetUserId()), username(request.getCoauthorUserId())
        )).toList();
    }

    @PostMapping("/{requestId}/accept")
    public RequestEntity accept(@PathVariable UUID requestId) {
        return service.respond(requestId, true);
    }

    @PostMapping("/{requestId}/reject")
    public RequestEntity reject(@PathVariable UUID requestId) {
        return service.respond(requestId, false);
    }

    @GetMapping("/coauthor/{storyId}")
    public List<CoauthorResponse> coauthors(@PathVariable UUID storyId) {
        return service.storyCoauthors(storyId).stream()
                .map(item -> new CoauthorResponse(item.getId(), item.getStoryId(), item.getUserId(),
                        users.findUserById(item.getUserId()).getUsername(), item.getCreatedAt()))
                .toList();
    }

    @GetMapping("/coauthor/stories")
    public List<StoryModel> coauthoredStories() {
        return service.myCoauthoredStories();
    }

    @DeleteMapping("/coauthor/{storyId}/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCoauthor(@PathVariable UUID storyId, @PathVariable UUID userId) {
        service.removeCoauthor(storyId, userId);
    }

    @PostMapping("/reactivate")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestEntity requestReactivation(@RequestBody ReactivationRequest request) {
        return service.requestReactivation(request.username(), request.userEmail());
    }

    @GetMapping("/admin/reactivations")
    public List<AdminReactivationResponse> pendingReactivations() {
        return service.pendingReactivations().stream().map(request -> {
            var user = users.findUserById(request.getSourceUserId());
            return new AdminReactivationResponse(request.getId(), user.getUsername(), user.getUserEmail(), request.getCreatedAt());
        }).toList();
    }

    @PostMapping("/admin/reactivations/{requestId}/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void beginReactivation(@PathVariable UUID requestId) {
        service.beginReactivation(requestId);
    }

    @PostMapping("/chapters/{chapterId}/lock")
    public Map<String, Object> acquireLock(@PathVariable UUID chapterId) {
        var lock = service.acquireChapterLock(chapterId);
        return Map.of("chapterId", lock.getChapterId(), "expiresAt", lock.getExpiresAt());
    }

    @DeleteMapping("/chapters/{chapterId}/lock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void releaseLock(@PathVariable UUID chapterId) {
        service.releaseChapterLock(chapterId);
    }

    public record InviteRequest(@NotBlank String username) {}
    public record ReactivationRequest(String username, String userEmail) {}
    public record CoauthorResponse(UUID id, UUID storyId, UUID userId, String username, java.time.LocalDateTime createdAt) {}
    public record RequestResponse(UUID id, com.lectoria_api.requests.domain.RequestType type,
                                  com.lectoria_api.requests.domain.RequestStatus status, UUID sourceUserId,
                                  UUID targetUserId, UUID storyId, UUID coauthorUserId,
                                  java.time.LocalDateTime createdAt, String sourceUsername,
                                  String targetUsername, String coauthorUsername) {}
    public record AdminReactivationResponse(UUID id, String username, String userEmail,
                                            java.time.LocalDateTime createdAt) {}

    private String username(UUID userId) {
        return userId == null ? null : users.findUserById(userId).getUsername();
    }
}
