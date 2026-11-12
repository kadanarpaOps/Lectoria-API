package com.lectoria_api.requests.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ChapterLockRepository extends JpaRepository<ChapterLockEntity, UUID> {
    void deleteByChapterIdAndUserId(UUID chapterId, UUID userId);
    void deleteAllByUserIdAndChapterIdIn(UUID userId, Iterable<UUID> chapterIds);
    void deleteAllByExpiresAtBefore(LocalDateTime now);
}
