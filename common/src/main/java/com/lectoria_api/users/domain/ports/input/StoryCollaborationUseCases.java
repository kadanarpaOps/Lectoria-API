package com.lectoria_api.users.domain.ports.input;

import java.util.UUID;

public interface StoryCollaborationUseCases {
    boolean canEditStory(UUID storyId);
    boolean hasCoauthors(UUID storyId);
    boolean isChapterLockOwnedByCurrentUser(UUID chapterId);
    boolean isChapterLockedByOtherUser(UUID chapterId);
}
