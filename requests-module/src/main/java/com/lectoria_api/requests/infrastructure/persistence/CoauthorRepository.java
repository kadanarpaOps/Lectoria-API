package com.lectoria_api.requests.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CoauthorRepository extends JpaRepository<CoauthorEntity, UUID> {
    boolean existsByStoryIdAndUserId(UUID storyId, UUID userId);
    List<CoauthorEntity> findAllByStoryIdOrderByCreatedAtAsc(UUID storyId);
    List<CoauthorEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);
    void deleteByStoryIdAndUserId(UUID storyId, UUID userId);
    boolean existsByStoryId(UUID storyId);
}
