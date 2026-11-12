package com.lectoria_api.requests.application;

import com.lectoria_api.requests.infrastructure.persistence.ChapterLockRepository;
import com.lectoria_api.requests.infrastructure.persistence.CoauthorRepository;
import com.lectoria_api.stories.domain.ports.output.StoryRepositoryPort;
import com.lectoria_api.users.domain.ports.input.SharedAuthUseCases;
import com.lectoria_api.users.domain.ports.input.StoryCollaborationUseCases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CollaborationAccessService implements StoryCollaborationUseCases {
    private final StoryRepositoryPort stories;
    private final CoauthorRepository coauthors;
    private final ChapterLockRepository locks;
    private final SharedAuthUseCases auth;

    @Override
    public boolean canEditStory(UUID storyId) {
        UUID userId = auth.getAuthUserId();
        return stories.findById(storyId)
                .map(story -> story.getUserId().equals(userId)
                        || coauthors.existsByStoryIdAndUserId(storyId, userId))
                .orElse(false);
    }

    @Override
    public boolean hasCoauthors(UUID storyId) {
        return coauthors.existsByStoryId(storyId);
    }

    @Override
    public boolean isChapterLockOwnedByCurrentUser(UUID chapterId) {
        UUID userId = auth.getAuthUserId();
        return locks.findById(chapterId)
                .filter(lock -> lock.getExpiresAt().isAfter(LocalDateTime.now()))
                .filter(lock -> lock.getUserId().equals(userId))
                .isPresent();
    }

    @Override
    public boolean isChapterLockedByOtherUser(UUID chapterId) {
        UUID userId = auth.getAuthUserId();
        return locks.findById(chapterId)
                .filter(lock -> lock.getExpiresAt().isAfter(LocalDateTime.now()))
                .filter(lock -> !lock.getUserId().equals(userId))
                .isPresent();
    }
}
