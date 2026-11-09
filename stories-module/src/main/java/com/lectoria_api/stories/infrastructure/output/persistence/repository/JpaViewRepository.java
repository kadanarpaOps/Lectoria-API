package com.lectoria_api.stories.infrastructure.output.persistence.repository;

import com.lectoria_api.stories.infrastructure.output.persistence.entities.ViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaViewRepository extends JpaRepository<ViewEntity, UUID> {
    Optional<ViewEntity> findByStoryIdAndUserId(UUID storyId, UUID userId);
    long countByStoryId(UUID storyId);
}
